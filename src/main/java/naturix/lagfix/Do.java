package naturix.lagfix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * @author Andre L Noel
 * @version 2015-03-19
 */
public class Do {

  public Do() {
    
  }
  
  // General Minecraft Modding Programming Tools
  
  /**Returns a string of all the data in the specified file. It is intended to conveniently load any text file into a string.
   * @param filePathAndName
   * @return String
   */
  public static String FileToString( String filePathAndName ) {
    Reader istream;
    try {
      istream = new FileReader(filePathAndName);
    } catch (FileNotFoundException e) {
      Err("FILE NOT FOUND for file " + filePathAndName);
      e.printStackTrace();
      return "";
    }
    StringBuffer sbuffer = new StringBuffer( );
    char[] b = new char[128];
    int n;
    try {
      while ((n = istream.read(b)) > 0) { sbuffer.append(b, 0, n); }
    } catch (IOException e) {
      Err("Could not read from file " + filePathAndName);
      e.printStackTrace();
    }
    try {
      istream.close();
    } catch (IOException e) {
      Err("Could not close the read stream for file " + filePathAndName);
      e.printStackTrace();
    }
    return sbuffer.toString();
  }
  
  /**Returns true if it successfully writes the file to storage with the string data. 
   * @param filename
   * @param filedata
   * @return boolean true if successful
   */
  public static boolean StringToFile( String filename, String filedata ) {
    Pattern pattern = Pattern.compile("^(.*)/");
    Matcher matcher = pattern.matcher(filename);
  	if ( matcher.find() ) { folderMake( matcher.group(1) ); } // create folder path if it doesn't exist
    File fh = new File(filename);
    if ( ! fh.exists() ) {
      try {
        fh.createNewFile();
      } catch (IOException e) {
        Err("Could not create file " + filename );
        e.printStackTrace();
        return false;
      }
    }
    OutputStream ostream;
    try {
      ostream = new FileOutputStream(filename);
    } catch (FileNotFoundException e1) {
      Err("Could not open file output stream for " + filename);
      e1.printStackTrace();
      return false;
    }
    // put filedata to the ostream here
    byte[] data = filedata.getBytes( );
    try {
      ostream.write(data, 0, data.length);
    } catch (IOException e1) {
      Err("Could not write data to file " + filename);
      e1.printStackTrace();
      try {
        ostream.close();
      } catch (IOException e) {
        Err("Could not close output stream for file " + filename + " after the above write error.");
        e.printStackTrace();
      }
      return false;
    }
    try {
      ostream.close();
    } catch (IOException e) {
      Err("Could not close output stream for file " + filename);
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  /**Returns true on success moving a folder and its contents to another folder location. 
   * If it fails part way through it will stop with the folder partially copied.  
   * It merges into the destination folder if it already exists.
   * It assumes reasonably small to medium file sizes at this time, 
   * but with large amounts of ram it may be fine with large files.
   * If you need perfect robustness, write another one. lol. 
   * Excellent for what it was made for.
   * @param fromPath
   * @param toPath
   * @return boolean true on success
   */
  public static boolean folderMove(String fromPath, String toPath) {
	  File fromPathFile = new File(fromPath);
	  String[] fileNameList = fromPathFile.list();
	  for (int i=0; i < fileNameList.length; i++) {
	    String fromPathPlus = fromPath +"/"+ fileNameList[i];
	    String toPathPlus = toPath +"/"+ fileNameList[i];
      File fromFile = new File(fromPathPlus);
		  File toFile = new File(toPathPlus);
		  if ( fromFile.isDirectory() ) { // is folder
			  toFile.mkdirs();
			  if ( folderMove(fromPathPlus,toPathPlus) ) {
				  fromFile.delete(); // the boolean value the .delete() returns is actually wrong. So no real error checking is possible using the return value.
			  } else { Do.Err("moveFolder("+fromPathPlus+", "+toPathPlus+") Failed."); return false; }
		  } else { // is file
			  String fileContent = Do.FileToString(fromPathPlus);
			  if ( fileContent.length() == fromFile.length() ) {
				  if ( Do.StringToFile(toPathPlus,fileContent) ) { // ASSUMES fairly small file sizes since the whole file content is read into RAM instead of in parts.
					  fromFile.delete();
				  } else { Do.Err("moveFolder("+fromPathPlus+", "+toPathPlus+") Failed to write destination file."); return false; }
			  } else { Do.Err("moveFolder("+fromPathPlus+", "+toPathPlus+") Failed to read source file."); return false;}
		  } // end is folder or is file
	  } // end for i fileList
	  fromPathFile.delete();
	  return true;
  }
  
  /**
   * Sends the mod name and a string message to the standard error output channel and to player chat if the player is not null.
   * @param player
   * @param s
   */
  public static void Err(EntityPlayer player, String s) {
    Say(player,"ERROR [" + LagFix.MODID + "] " + s);
    Err(s);
    return;
  }

  /**
   * Sends the mod name and a string message to the standard error output channel.
   * @param s
   */
  public static void Err(String s) {
    System.err.println("ERROR [" + LagFix.MODID + "] " + s);
    return;
  }
  
  /**
   * You may disable all Trace() output by setting this to false.
   * Defaults to true to display all Trace() messages.  This is
   * a way to keep debugging messages throughout your code and be able
   * to easily switch them all off for release versions of your program.
   * The Trace() functions display a string message in the console and
   * in player chat if specified.
   */
  public static boolean tracing = true;

  /**
   * Outputs a string with mod name and "TRACE" in the console. Also it outputs the same text to the player chat, if the player is not null.
   * @param player
   * @param s
   */
  public static void Trace(EntityPlayer player, String s) {
    if (!tracing) { return; }
    Say(player,"TRACE [" + LagFix.NAME + "] " + s);
    Trace(s);
  }
  
  /**
   * Outputs a string with mod name and "TRACE" in the console.
   * @param s
   */
  public static void Trace(String s) {
    if (!tracing) { return; }
    System.out.println("TRACE [" + LagFix.MODID + "] " + s);
    //System.err.println("TRACE [" + ModInfo.ID + "] " + s); // using the standard error output because it shows up in red in my console *grin*
  }
  
  /**
   * Displays text message in chat to the player, if parameter player is not null.
   * @param player
   * @param s
   */
    public static void Say(EntityPlayer player, String s) { 
      if (player != null) { 
        player.sendMessage(new TextComponentString(s)); // mc1.7.10 and mc1.7.2 and mc1.8
        //player.addChatMessage(s); // mc1.6.4
      } 
    }
  
  /**
   * Returns true if the player is an op (operator). With a custom exception that if the SpawnCommands misc config setting "allow With Cheats Disabled" is true (the default) then in single player it also returns true, as if the player is an op despite the "no cheats allowed" option at game creation.
   * @param player
   */
    public static boolean isOp(EntityPlayer player) {
        // test if "cheats enabled: on" and is in single player then consider the player an op
        if ( ((EntityPlayerMP)player).mcServer.isSinglePlayer() && player.canUseCommand(2,"gamemode") ) { return true; }
        // mc1.8.9 && MinecraftServer.getServer().getConfigurationManager().canSendCommands(player.getGameProfile()) ) { return true; }
        // actual test for op:
        return hasItemInArray(player.getName(), ((EntityPlayerMP)player).mcServer.getPlayerList().getOppedPlayerNames()); // mc1.9, mc1.10
        //return hasItemInArray(player.getName(),MinecraftServer.getServer().getConfigurationManager().getOppedPlayerNames()); // mc1.8 and mc1.8.9
        //return MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile()); // mc1.7.10
        //return MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(player.getGameProfile().getName()); // mc 1.7.2 ???
        //return MinecraftServer.getServerConfigurationManager(MinecraftServer.getServer()).isPlayerOpped(player.username); // mc 1.6.4
      }
  
  /**Returns true if it exists and it is a folder. 
   * @param filePathAndName
   * @return boolean
   */
  public static boolean folderExists(String filePathAndName) {
    File fh = new File(filePathAndName);
    if (fh.exists() && fh.isDirectory()) { return true; }
    return false;
  }
  
  /**Returns true if it exists and it is a data file, not a folder.
   * @param filePathAndName
   * @return boolean
   */
  public static boolean fileExists(String filePathAndName) {
    File fh = new File(filePathAndName);
    if (fh.exists() && !fh.isDirectory()) { return true; }
    return false;
  }
  
  /**
   * Attempts to delete the file.  Returns true if successful.
   * @param filePath
   */
  public static boolean fileDelete(String filePath) {
    File fh = new File(filePath);
    return fh.delete();
  }

  /**Returns the numeric long of the date the file was last modified.
   * @param filePathAndName
   * @return
   */
  public static long fileDateModified(String filePathAndName) {
  	File fh = new File(filePathAndName);
  	return fh.lastModified();
  }
  
/**
 * Attempts to create all folders specified in path, if they do not exist.
 * @param filePath
 */
  public static boolean folderMake (String filePath) { 
    File fh = new File(filePath);
    return fh.mkdirs();
  }

  // Configuration Array system: String[n][2] contains variable names and values.  All are string.  Names are case insensitive and my include spaces between other displayable characters.  When stored in a file the names and values are escaped with %hh for non standard characters.
  	
  	/** Returns true if the value of the variable in the configuration array equalsIgnoreCase any of these 
  	 * "true","t","1","1.0","yes","y","on","enable","enabled","allow","allowed","correct". 
  	 * List is arbitrary. Source code may be edited if needed.  
  	 * Returns false under any other circumstance.  
  	 * @param configArray
  	 * @param varName
  	 * @return boolean
  	 */
  	public static boolean getConfigArrayValueBoolean( String[][] configArray, String varName) {
  		return getBoolean(getConfigArrayValue( configArray, varName ));
  	}
  
  	/**Returns a string of the value for variable in a configuration array.
  	 * @param configArray
  	 * @param varName
  	 * @return string
  	 */
  	public static String getConfigArrayValue( String[][] configArray, String varName) {
  		for(int i = (configArray.length - 1); i >= 0; i--) { // if there are duplicate entries this takes the value of the last one
  			if ( configArray[i][0].equalsIgnoreCase(varName) ) { return configArray[i][1]; }
  		}
  		configArray = null;
  		return "";
  	}
  	
  	/** Returns a configuration array with a changed value for a config variable name. If the variable name is not in the configuration passed to this, it will add it and the new value. This will return a configuration array with the change. It does not change the original array. 
  	 * Suggestion: use this by assigning your config array to this function. Example: myConfigArr = setConfigArrayValue( myConfigArr, "enableMyFeature", "true" );    
  	 * @param configArray
  	 * @param varName
  	 * @param varValue
  	 * @return a new updated configArray
  	 */
  	public static String[][] setConfigArrayValue( String[][] configArray, String varName, String varValue ) {
  		String[][] outputArray = null;
  		boolean foundVarName = false;
  		for(int i = 0; i < configArray.length; i++) {
  			if ( configArray[i][0].equalsIgnoreCase(varName) ) {
  				configArray[i][1] = varValue;
  				foundVarName = true;
  			} // allow to loop through all in case there are duplicate variable names
  		}
  		if (foundVarName) {
  			return configArray;
  		} else {
  			outputArray = new String[configArray.length + 1][2]; // the output array is now a different size than the original
  			outputArray[configArray.length][0] = varName; // add the new variable on the end of the array
  			outputArray[configArray.length][1] = varValue;
  			for(int i = 0; i < configArray.length; i++) {
  	  			outputArray[i][0] = configArray[i][0]; // include all the original variables
  	  			outputArray[i][1] = configArray[i][1];
  			}
  			configArray = null;
  			return outputArray;
  		}
  		
  	}
  	
  	/**Returns a changed config array without the config variable name specified. 
  	 * Suggestion: use this by assigning your config array to this function. Example: myConfigArr = setConfigArrayValue( myConfigArr, "variableNameToRemove" );
  	 * @param configArray
  	 * @param varName
  	 * @return a new updated configArray
  	 */
  	public static String[][] removeConfigArrayVariable( String[][] configArray, String varName ) {
  		String[][] outputArray = null;
  		int foundVarName = 0;
  		for(int i = 0; i < configArray.length; i++) {
  			if ( configArray[i][0].equalsIgnoreCase(varName) ) { // case insensitive variable names
  				foundVarName++;
  			} // allow to loop through all in case there are duplicate variable names
  		}
  		if (foundVarName == 0) { // variable name not found, no change needed
  			return configArray;
  		} else {
  			outputArray = new String[configArray.length - foundVarName][2]; // the output array is now a different size than the original
  			int outputArrayIndex = 0;
  			for(int i = 0; i < configArray.length; i++) {
  				if (! configArray[i][0].equalsIgnoreCase(varName) ) { // include all the other original variables
	  				outputArray[outputArrayIndex  ][0] = configArray[i][0];
	  	  		outputArray[outputArrayIndex++][1] = configArray[i][1];
  				}
  			}
  			configArray = null;
  			return outputArray;
  		}
  	}
  	
	/** Reads a configuration file containing pairs of variable names and values into a 2 dimensional string array. Comment lines start with // and are ignored. Example line in file: NumberOfElephants=5
	 * @param filename
	 * @return String[n][2] a string array of configuration variable names and values
	 */
	public static String[][] getConfigArrayFile(String filename) {
		String[][] tempDataArray = null;
		String[][] finalDataArray = null;
		String varName = null;
		String varValue = null;
		int usableArrayLines = 0;
		String[] rawDataLines = FileToString(filename).replaceAll("\r","\n").split("\n"); // load data. fix windows return vs newline.
		tempDataArray = new String[rawDataLines.length][2];
		// clean and count usable lines
		for(int rawDataLine = 0; rawDataLine < rawDataLines.length; rawDataLine++) { 
			// clean the config file line
			rawDataLines[rawDataLine] = rawDataLines[rawDataLine].replaceAll("\t"," "); // change tabs to spaces
			rawDataLines[rawDataLine] = rawDataLines[rawDataLine].replaceAll("^\\s+",""); // remove white space at beginning of line
			rawDataLines[rawDataLine] = rawDataLines[rawDataLine].replaceFirst("^//.*","");// remove comments.  they start with //
			rawDataLines[rawDataLine] = rawDataLines[rawDataLine].replaceAll("\\s+$",""); // remove white space at end of line
			rawDataLines[rawDataLine] = rawDataLines[rawDataLine].replaceFirst("\\s*=\\s*","=");// remove white space around first equal sign (=)
			if ( rawDataLines[rawDataLine].equals("") ) { continue; }
			// parse config file data line
			boolean hasNoVarNameAndEquals = true;
			if ( ! rawDataLines[rawDataLine].startsWith("=") ) {
				for(int i = 0; i < rawDataLines[rawDataLine].length(); i++){
					if ( rawDataLines[rawDataLine].substring(i, i+1).equals("=")) {
						varName  = rawDataLines[rawDataLine].substring(0,i).toLowerCase();// the toLowerCase helps makes variable names case insensitive 
						varValue = rawDataLines[rawDataLine].substring(i + 1);
						hasNoVarNameAndEquals = false;
						i = rawDataLines[rawDataLine].length(); // end the i loop
					}
				}
			} // end if not starts with "="
			// store and count the data
			if ( hasNoVarNameAndEquals ) { continue; }
			tempDataArray[usableArrayLines][0]=varName;
			tempDataArray[usableArrayLines++][1]=varValue; 
		} // end for raw lines
		finalDataArray =  new String[usableArrayLines][2]; // create an array of the correct size
		for(int i = 0; i < usableArrayLines; i++) {
			for(int j = 0; j < 2; j++) {
				finalDataArray[i][j] = tempDataArray[i][j]; // put into the array of the correct size
			}
		}
		tempDataArray = null;
		return finalDataArray;
	}
	
	/**
	 * Writes dataArray to a configuration file.  The array should be composed of variable name and value pairs. Example: dataArray[0][0]="NumberOfElephants"; dataArray[0][1]="5"; dataArray[1][0]="canElephantsFly"; dataArray[1][1]="no"; 
	 * @param fileName
	 * @param dataArray
	 * @return true if successful
	 */
	public static boolean putConfigArrayFile(String fileName, String[][] dataArray) {
		String fileData = "";
		for(int i = 0; i < dataArray.length; i++) 
		{ fileData += escape(dataArray[i][0]) +"="+ escape(dataArray[i][1]) + "\n"; }
		return Do.StringToFile(fileName, fileData);
	}
	
	
	/**Returns true if the string is any one of a set of strings meaning true.  Such as "true","t","1","1.0","1.","yes","y","on","enable","enabled","allow","allowed","correct".  Otherwise it returns false.
	 * @param stringValue
	 * @return boolean
	 */
	public static boolean getBoolean( String stringValue ) {
		stringValue = stringValue.replaceAll("^\\s+",""); // remove leading white space
		stringValue = stringValue.replaceAll("\\s+$",""); // remove trailing white space
  		final String[] trues = { "true","t","1","1.0","1.","yes","y","on","enable","enabled","allow","allowed","correct" }; // arbitrary and may be edited
  		for(int i = 0; i <  trues.length; i++) {
  			if (stringValue.equalsIgnoreCase(trues[i])) { stringValue = null; return true; }
  		}
  		stringValue = null; // clear from memory in case the content is large.
  		return false;
	}
	
	/** Returns the string decoded to its original from %hh and "+" encoding. See also escape(string).   
	 * @param string
	 * @return
	 */
	public static String unEscape( String string ) {
		try { return java.net.URLDecoder.decode(string, "ISO-8859-1"); } 
		catch (UnsupportedEncodingException e) { return string; }
	}
	
	/** Returns the string with all non-standard bytes encoded in hex with prefixed escape character "%" with the exception that spaces are encoded as "+". See also unEscape(string).
	 * @param string
	 * @return
	 */
	public static String escape( String string ) {
		try { return java.net.URLEncoder.encode(string, "ISO-8859-1"); } 
		catch (UnsupportedEncodingException e) { return string; }
	}
	

  /**
    * Returns true if the item is in the array.
    * @param theItem
    * @param theArray
    * @return
    */
   public static Boolean hasItemInArray(String theItem, String[] theArray) {
	 for (int i=0;i < theArray.length; i++) {
       if ( theArray[i].equals(theItem) ) { return true; }
     }
     return false;
   }

}
