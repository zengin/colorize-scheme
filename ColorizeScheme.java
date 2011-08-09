import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ColorizeScheme {
	static ArrayList<Character> stack;

	public static void main(String[] args) {
		/*
		 *  check arguments and deal with the filename issues
		 */
		String outFileName = "";
		if (args.length == 0 || args.length > 2) {
			System.out
					.println("usage: java ColorizeScheme <input_file> [<output_file>]");
			System.exit(-1);
		} else if (args.length == 2) {
			if (args[1].endsWith(".htm") || args[1].endsWith(".html")) {
				outFileName = args[1];
			} else {
				outFileName = args[1] + ".html";
			}
		} else {
			StringTokenizer st = new StringTokenizer(args[0], ".");
			outFileName = st.nextToken() + ".html";
		}
		File file = new File(args[0]);
		
		String[] COLORS = { "#000099", "#990000", "#009900", "#000000" };
		int colorCounter = 0;
		String commentColor = "#000000";
		boolean comment = false;
		int lineCount = 0;

		/*
		 *  initialize stack
		 */
		stack = new ArrayList<Character>();
		
		try {
			Scanner scan = new Scanner(file);
			String string = "";
			String output = "<html>\n<head>\n<title>Colorized " + args[0]
					+ "</title>\n</head>\n<body bgcolor=#FFFFFF>\n<pre>\n";
			while (scan.hasNextLine()) {
				/*
				 *  read input file, line by line.
				 */
				string = scan.nextLine();
				lineCount++;
				
				for (int i = 0; i < string.length(); i++) {
					/*
					 * go through every char in the line
					 */
					switch ((char) string.charAt(i)) {
					case ';':
						if(!comment){
							comment = true;
							output += "<font color=" + commentColor + "><b>;";
						}else{
							output += ";";
						}
						break;
					case '(':
					case '[':
						if (!comment) {
							handleParanthesis(string.charAt(i));
							output += "<font color=" + COLORS[colorCounter]
									+ ">" + (char) string.charAt(i);
							colorCounter = (colorCounter + 1)
									% COLORS.length;
						}
						break;
					case ')':
					case ']':
						if (!comment) {
							if(handleParanthesis(string.charAt(i))){
								output += (char) string.charAt(i) + "</font>";
								colorCounter = (colorCounter - 1 + COLORS.length)
										% COLORS.length;
							}else{
								errorMessage("[ERROR] unexpected closing paranthesis " + string.charAt(i) + " at line " + lineCount, string, i);
							}
						}
						break;
					case ' ':
					case '\t':
						output += (char) string.charAt(i);
						break;
					default:
						if(!comment && stack.size() == 0 ){
							errorMessage("[ERROR] Expecting an opening paranthesis, found " + string.charAt(i) + " at line " + lineCount ,string, i);
						}
						output += (char) string.charAt(i);
						break;
					}
				}
				/*
				 *  put html end of line i.e. <br> and finished marking as comment if needed
				 */
				if (comment){
					output += "</b></font>\n";
					comment = false;
				}else{
					output += "\n";
				}
			}
			output += "\n</pre>\n</body>\n</html>";
			
			/*
			 * Print error message if stack is not empty
			 */
			if(stack.size() != 0){
				errorMessage("[ERROR] There are unmatched paranthesis in the code.");
			}
			
			/*
			 * Write output to the file
			 */
			System.setOut(new PrintStream(new File(outFileName)));
			System.out.println(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * handles parantheses in the input.
	 * @param input
	 * @return true if a new opening paranthesis is pushed to the stack or 
	 * a matching paranthesis is popped out of the stack. false otherwise.
	 */
	public static boolean handleParanthesis(char input){
		switch(input){
		case '[':
		case '(':
			push(input);
			break;
		case ')':
			if(stack.size() == 0){
				return false;
			}else if (pop() != '('){
				return false;
			}else{
				return true;
			}
		case ']':
			if(stack.size() == 0){
				return false;
			}else if (pop() != '['){
				return false;
			}else{
				return true;
			}
		}
		return true;
	}
	
	/**
	 * pushes input char into the stack	
	 * @param input
	 */
	public static void push(char input){
		stack.add(0, input);
	}
	
	/**
	 * pops the top element out of the stack
	 * @return
	 */
	public static char pop(){
		return stack.remove(0);
	}
	
	
	/**
	 * Prints the result in a well-shaped form
	 * @param message error message
	 * @param line all the text in the line where the error is found
	 * @param position at which character of the line the error is found
	 */
	public static void errorMessage(String message, String line, int position){
		message += "\n" + line + "\n";
		for(int i = 0; i < position; i++){
			message += " ";
		}
		message += "^";
		System.err.println(message);
		System.exit(-1);
	}
	
	/**
	 * Prints an error message of one line
	 * @param message
	 */
	public static void errorMessage(String message){
		System.err.println(message);
		System.exit(-1);
	}
}