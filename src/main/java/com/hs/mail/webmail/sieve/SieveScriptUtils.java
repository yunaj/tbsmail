package com.hs.mail.webmail.sieve;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jsieve.Argument;
import org.apache.jsieve.Arguments;
import org.apache.jsieve.BaseSieveContext;
import org.apache.jsieve.Command;
import org.apache.jsieve.Commands;
import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.SieveConfigurationException;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.SieveParserVisitorImpl;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.Test;
import org.apache.jsieve.TestList;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.parser.generated.ASTstart;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.parser.generated.SieveParser;
import org.apache.jsieve.parser.generated.SieveParserVisitor;
import org.apache.jsieve.parser.generated.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.config.Configuration;

public class SieveScriptUtils {
	
	private static Logger logger = LoggerFactory.getLogger(SieveScriptUtils.class);

	private static final String DEFAULT_SIEVE = "default.sieve";

	private static SieveContext context = null;
	static {
		try {
			ConfigurationManager config = new ConfigurationManager();
			context = buildSieveContext(config);
		} catch (SieveConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private static SieveContext buildSieveContext(ConfigurationManager config) {
		return new BaseSieveContext(config.getCommandManager(),
				config.getComparatorManager(), 
				config.getTestManager(),
				config.getLog()); 
	}
	
	public static void writeScript(String identity, List<WmaFilterItem> commands)
			throws IOException {
		Writer writer = getWriter(identity);
		try {
			ScriptWriter factory = new ScriptWriter(writer);
			factory.writeScript(commands);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static List<WmaFilterItem> readScript(String identity) {
		InputStream input = null;
		try {
			input = getInputStream(identity);
			return readScript(input);
		} catch (FileNotFoundException skip) {
		} catch (ParseException ex) {
			logger.error(ex.getMessage());
		} finally {
			IOUtils.closeQuietly(input);
		}
		return null;
	}
	
	public static List<WmaFilterItem> readScript(InputStream input)
			throws ParseException {
		return new ScriptParser().parseScript(input);
	}
	
	private static Writer getWriter(String identity) throws IOException {
		File directory = Configuration.getUserHome(identity);
		FileUtils.forceMkdir(directory);
		FileOutputStream os = new FileOutputStream(new File(directory, DEFAULT_SIEVE));
		return new OutputStreamWriter(os, "UTF-8");
	}
	
	private static InputStream getInputStream(String identity)
			throws FileNotFoundException {
		File directory = Configuration.getUserHome(identity);
		return new FileInputStream(new File(directory, DEFAULT_SIEVE));
	}

	static class ScriptWriter {
		
		private Writer writer;
		
		public ScriptWriter(Writer writer) {
			this.writer = writer;
		}
		
		public void writeScript(List<WmaFilterItem> commands)
				throws IOException {
			for (int i = 0; i < commands.size(); i++) {
				if (i > 0) {
					writer.write("\n");
				}
				writeScript(commands.get(i));
			}
		}

		private void writeScript(WmaFilterItem command) throws IOException {
			List<String> testList = new ArrayList<String>();
			appendTest(testList, "\"Subject\"", command.getSubject());
			appendTest(testList, "\"From\"", command.getSender());
			if (testList.isEmpty()) {
				return;
			}
			if (testList.size() == 1) {
				writer.write("if " + testList.get(0));
			} else {
				writeTestList(command, testList);
			}
			writeAction(command);
		}
		
		private void appendTest(List<String> testList, String header,
				String value) {
			if (StringUtils.isNotBlank(value)) {
				String[] values = StringUtils.split(value, ',');
				testList.add(new StringBuffer("header :contains ")
						.append(header)
						.append(" ")
						.append((values.length > 1) ? toStringList(values)
								: toString(value)).toString());
			}
		}
		
		private void writeTestList(WmaFilterItem command, List<String> testList)
				throws IOException {
			writer.write("if ");
			writer.write(command.getMatch());
			writer.write(" (");
			writeTestList(testList);
			writer.write(")");
		}
		
		private void writeTestList(List<String> testList) throws IOException {
			for (int i = 0; i < testList.size(); i++) {
				if (i > 0) {
					writer.write(",\n\t");
				}
				writer.write(testList.get(i));
			}
		}
		
		private void writeAction(WmaFilterItem command) throws IOException {
			writer.write("\n{\n\t");
			writer.write(command.getAction());
			if (StringUtils.isNoneBlank(command.getActionparam())) {
				writer.write(" \"");
				writer.write(command.getActionparam());
				writer.write("\"");
			}
			writer.write(";\n}");
		}
		
		private static String toStringList(String[] strs) {
			StringBuffer sb = new StringBuffer();
			sb.append('[');
			for (int i = 0; i < strs.length; i++) {
				if (i == 0) {
					sb.append(',');
				}
				sb.append(toString(strs[i]));
			}
			sb.append(']');
			return sb.toString();
		}
		
		private static String toString(String str) {
			if (str == null) {
				return null;
			}
			int sz = str.length();
			StringBuffer sb = new StringBuffer(sz);
			sb.append('"');
			for (int i = 0; i < sz; i++) {
				char ch = str.charAt(i);
				switch (ch) {
				case '"':
					sb.append('\\');
					sb.append('"');
					break;
				case '\\':
					sb.append('\\');
					sb.append('\\');
					break;
				default:
					sb.append(ch);
				}
			}
			sb.append('"');
			return sb.toString();
		}
		
	}
	
	static class ScriptParser {

		public List<WmaFilterItem> parseScript(InputStream input)
				throws ParseException {
			List<WmaFilterItem> items = new ArrayList<WmaFilterItem>();
			try {
				final SimpleNode node = new SieveParser(input, "UTF-8").start();
				SieveParserVisitor visitor = new WmaSieveParserVisitor();
				Commands commands = (Commands) node.jjtAccept(visitor, null);
				if (commands != null) {
					List children = commands.getChildren();
					for (int i = 0; i < children.size(); i++) {
						WmaFilterItem item = parseFilterItem((Command) children
								.get(i));
						if (item != null) {
							items.add(item);
						}
					}
				}
				return items;
			} catch (SieveException ex) {
				throw new ParseException(ex.getMessage());
			}
		}
		
		private WmaFilterItem parseFilterItem(Command command) {
			if (!"if".equals(command.getName())) {
				return null;
			}
			WmaFilterItem item = new WmaFilterItem();
			Arguments arguments = command.getArguments();
			if (hasTests(arguments)) {
				Test test = (Test) arguments.getTestList().getTests().get(0);
				if ("anyof".equals(test.getName())
						|| "allof".equals(test.getName())) {
					item.setMatch(test.getName());
					TestList testList = test.getArguments().getTestList();
					if (testList != null) {
						Iterator it = testList.getTests().iterator();
						while (it.hasNext()) {
							if (!parseTest(item, (Test) it.next())) {
								return null;
							}
						}
					}
				} else if (!parseTest(item, test)) {
					return null;
				}
			}
			if (!parseAction(item, command.getBlock().getChildren())) {
				return null;
			}
			return item;
		}

		private boolean parseTest(WmaFilterItem item, Test test) {
			String name = test.getName();
			if ("header".equals(name)) {
				return parseHeader(item, test.getArguments().getArgumentList());
			}
			return false;
		}
		
		private boolean parseHeader(WmaFilterItem item, List args) {
			if (args.size() != 3) {
				return false;
			}
			String h = getArgument((Argument) args.get(1));
			String v = getArgument((Argument) args.get(2));
			if ("subject".equalsIgnoreCase(h)) {
				item.setSubject(v);
			} else if ("from".equalsIgnoreCase(h)) {
				item.setSender(v);
			}
			return true;
		}
		
		private boolean parseAction(WmaFilterItem item, Commands commands) {
			List children = commands.getChildren();
			if (children.size() != 1) {
				return false;
			}
			return parseAction(item, (Command) children.get(0));
		}
		
		private boolean parseAction(WmaFilterItem item, Command command) {
			item.setAction(command.getName());
			if (hasArguments(command.getArguments())) {
				Argument arg = (Argument) command.getArguments()
						.getArgumentList().get(0);
				item.setActionparam(getArgument(arg));
			}
			return true;
		}
		
		private String getArgument(Argument arg) {
			if (arg instanceof StringListArgument) {
				List args = ((StringListArgument) arg).getList();
				return StringUtils.join(args.iterator(), ',');
			} else {
				return arg.getValue().toString();
			}
		}
		
		private boolean hasTests(Arguments arguments) {
			TestList testList = arguments.getTestList();
			return testList != null && testList.getTests() != null
					&& testList.getTests().size() > 0;
		}
		
		private boolean hasArguments(Arguments arguments) {
			return arguments != null && arguments.getArgumentList() != null
					&& arguments.getArgumentList().size() > 0;
		}
		
	}
	
	static class WmaSieveParserVisitor extends SieveParserVisitorImpl {

		public WmaSieveParserVisitor() {
			super(context);
		}

		@Override
	    public Object visit(ASTstart node, Object data) throws SieveException {
	        // Start is an implicit Block
	        // There will be one child, an instance of Commands
	        List children = new ArrayList(node.jjtGetNumChildren());
	        return (Commands) ((List) node.childrenAccept(this,
	                children)).get(0);
	    }
		
	}
	
}
