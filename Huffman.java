package AlgorithmsFinal;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;


public class Huffman {
	static int [] frequencyArray;
	static Character check;
	static int remaining;
	static  int bytesLength;
	static StringBuilder encodedString = new StringBuilder();
	static StringBuilder encodedStringFolder = new StringBuilder();
	static StringBuilder compressedCode = new StringBuilder();
	static StringBuilder string = new StringBuilder();
	static StringBuilder OutText = new StringBuilder();
	static TreeMap<Character, String> charaHuffmancodePair = new TreeMap<>(); 
	static TreeMap<String, Character> CodeCharacterPair = new TreeMap<>(); 
	
	static PriorityQueue<node> pQueue = new PriorityQueue<>((o1, o2) -> (o1.frequency < o2.frequency) ? -1 : 1);
	
	
	static class node
	{
	   node left,right;
	   int frequency;
	   String character;
	   
	   //constructor to make new node
	   public node(int value,String character) {
		   frequency = value;
		   this.character = character;
		   this.left=null;
		   this.right=null;
		   
	   }
	   //constructor to add the 2 least freqs
	   public node(node right,node left) {
		   frequency = left.frequency + right.frequency;
	        character = left.character + right.character;
	        if (left.frequency < right.frequency) { //as binary tree
	            this.right = right;
	            this.left = left;
	        } else {
	            this.right = left;
	            this.left = right;
	        }
		   
	   }
	}//end class node
	
	public static void main (String args[]) throws Exception
	{
	  Scanner sc = new Scanner(System.in);
	  
	  System.out.print("Enter 1 to Compress File \n" + "Enter 2 to Decompress File \n" + "Enter 3 to Compress Folder \n"+ "Enter 4 to Decompress Folder \n");
	  int choice = sc.nextInt();
	  
	  System.out.println("Please enter the path of the File:");
	  String FileName =sc.next();
	   
	  switch(choice)
	  { 
	  	case 1:
	  	{  
	  		String text = new String(Files.readAllBytes(Paths.get(FileName)));
	  		int textLength = text.length();
	  		long startTime = 0;
	  		
	  		//calculate the frequency of each character and add the character& frequency to the priority queue
			if(textLength == 0)
			{
				System.out.print("Invalid File Name!");
			}
			else
			{ 	startTime = System.nanoTime();
				frequencyArray = new int[256];
				for(int i=0;i<textLength;i++)
					frequencyArray[text.charAt(i)]++;
				
				for(int i=0;i<frequencyArray.length;i++)
					if(frequencyArray[i]>0) 
						 pQueue.add(new node(frequencyArray[i],((char)i) + "")); //add new node into priority queue	
			}
			
			//build huffman tree using frequencies of characters
			 BuildTree(pQueue);
			 
			//create huffman code
			recursiveGetHuffmanCode(pQueue.peek(), "");
			
			 long finishTime = System.nanoTime();
	         long time = finishTime - startTime;
	         System.out.println("Time in nanosecond is "+time);
			
			//get encoded string
			 for(int i=0;i<text.length();i++) 
			      encodedString.append(charaHuffmancodePair.get(text.charAt(i)));
 
			//write in the file the byte count and remaining bits
			int remainingBits = 8 - (encodedString.length() % 8);
			int NoOfBytes = encodedString.length() / 8;
  			String content = "";
  			content+= NoOfBytes +"\n";
  			content+=remainingBits +"\n";
  			content+="Header\n";
              for(Map.Entry code:charaHuffmancodePair.entrySet()) {
                 content+= ""+  code.getKey() +"-->"+ code.getValue()+"-->"+ frequencyArray[(code.getKey().toString().charAt(0))] + '\n';
              }
              content += "Body\n";
              Files.writeString(Paths.get(FileName.replace("input.txt","output.txt")),content,StandardOpenOption.CREATE);
              
              //add padding
              for(int j=0;j<remainingBits;j++)
            	  encodedString.append(0);
              
              //compress file
              writeOut Out = new writeOut(Paths.get(FileName.replace("input.txt","output.txt"))+"");
              for (int i = 0; i < encodedString.length(); i++) {
  				if (encodedString.charAt(i) == '0')
  					Out.writeBit(false); //Write 0 to the file
  				else
  					Out.writeBit(true); //Write 1 to the file
  			}
             Out.close();
                    
             //printing each character and its ASCII code and Huffman code
               System.out.println("character"+"    "+"ASCII"+"          "+"Code");
                    for(Map.Entry code:charaHuffmancodePair.entrySet()) {
                    	 char c = code.getKey().toString().charAt(0);	  
                    	 String ASCII="0"+ Integer.toBinaryString((int) c);
                      	 System.out.println(code.getKey()+"            "+ASCII+"         "+code.getValue());             
                     }
                    
               File inFile = new File(Paths.get(FileName).toString());
               File compressedFile = new File(Paths.get(FileName.replace("input.txt","output.txt")).toString());      
  			   double comprassionRatio =(1.0*(compressedFile.length()) / (inFile.length()) ) * 100;
  		       System.out.println("The compression ratio is   "+comprassionRatio);
                             			  		
	  	}break;
	  	//end case 1
	  	
	  	case 2:
	  	{  
	          BufferedReader reader = new BufferedReader(new FileReader(FileName));
	          String myCharacters = "";
	          String myCodes =  "";
	          boolean flag= false ;
	          bytesLength = Integer.parseInt(reader.readLine());
	          remaining = Integer.parseInt(reader.readLine());
	          String line = reader.readLine(); 
	          
	          while (line != null) {

					 if(Objects.equals(line, "Header"))
		                  flag = true;
		              if(Objects.equals(line, "Body"))
		                  flag = false;
		              
		              //Read Header
		              if(flag)
		              {
		                  String [] tempArray =line.split("-->");
		                  if(tempArray.length == 3)
		                  {
		                	  if (tempArray[0].length() == 0) {
								
								 myCharacters = myCharacters+System.getProperty("line.separator")+"~";
								 myCodes = myCodes+tempArray[1]+"~";
								
	                          	}
		                	  else {
	                          		myCharacters = myCharacters+tempArray[0]+"~";
	                          		myCodes = myCodes+tempArray[1]+"~";
	                          	}	  
		                  }

		              }
		              else
		              {
		                	break;
		               }
		             
					line = reader.readLine();
					
				}//end while
	            reader.close();
	            

	          //create map for characters and their codes
	          String [] charArray = myCharacters.split("~");
	          String [] CodeArray = myCodes.split("~");
	          
	          for(int i=0;i<charArray.length;i++)
	        	  CodeCharacterPair.put(CodeArray[i], charArray[i].charAt(0)); 
	          
	       	 //Reading the body and Decompressing it
	           byte[] File = Files.readAllBytes(Paths.get(FileName));
	  	       int fileSize = File.length;
	           StringBuilder BinaryDecompress = getCompressedString(File, fileSize, bytesLength);
	           int length = BinaryDecompress.length()-1;
	          	  
	          	  //remove padding
	          	  BinaryDecompress.delete(length-remaining,length);
	          	
	         
	          	//decoding the decompressed string
	          	 long starttime = System.nanoTime();
	          	  String tempString = "";
	          	  for(int i=0;i<BinaryDecompress.length();i++)
	          	  {
	          		 tempString+=BinaryDecompress.charAt(i);
	          		 for(Map.Entry code:CodeCharacterPair.entrySet())
	          		 {
	       			    if(code.getKey().equals(tempString))
	       			    {
	       				  check = code.getValue().toString().charAt(0);
	       				  OutText.append(check);
	       				  tempString = "";
	       				  break;   				  
	       			    }
	          		 } 
	          	 }
	              long finishtime = System.nanoTime();
		          long time = finishtime - starttime;
		          System.out.println("Time in nanosecond is  "+time);
 	          		 
	          	Files.writeString(Paths.get(FileName.replace("output.txt","input.txt")),OutText,StandardOpenOption.CREATE);       	  	  

	  	}break;
	  	//end case 2
	  	case 3:
	  	{  	 Path path = Paths.get(FileName);
	  		 File file = new File(path+"");
	  		 File filesList[] = file.listFiles();
	  		 int sizeEncoded[];
	  		 int remainingBits[] = new int[4] ;
	  		 int NoofBytes[] = new int[4] ;
	  		 
	  		 StringBuilder encoded1 = new StringBuilder();
	  		 StringBuilder encoded2 = new StringBuilder();
	  		 StringBuilder encoded3 = new StringBuilder();
	  		 
	         int i = 0,j = 0;
	         String text[] = new String[3];
	         int freqArray[] = new int[256];
	         while (i<filesList.length) { 

	 	  		text[i] = new String(Files.readAllBytes(Paths.get(filesList[i].toString())));
	 	  		int textLength = text[i].length();
	 	  		
	 	  		//calculate the frequency of each character and add the character& frequency to the priority queue
	 			if(textLength == 0)
	 				System.out.print("Invalid File Name!");
	 			else
	 			{ 	
	 				for(j=0;j<textLength;j++)
	 					freqArray[text[i].charAt(j)]++;
	 			}
	 			i++;
	 			
	         }//end while
	         
	         //add frequencies and character to pqueue
	         for(j=0;j<freqArray.length;j++) {
					if(freqArray[j]>0) 
						 pQueue.add(new node(freqArray[j],((char)j) + "")); //add new node into priority queue	
	         }
	 
	 			//build huffman tree using frequencies of characters
	 			 BuildTree(pQueue); 
	 			//create huffman code
	 			recursiveGetHuffmanCode(pQueue.peek(), "");
	 			
	 			int m= 0;
	 			while(m<filesList.length)
	 			{
	 				for(j=0;j<text[m].length();j++) {
	 					if(m == 0)
					      encoded1.append(charaHuffmancodePair.get(text[m].charAt(j)));
	 					else if(m==1)
	 					  encoded2.append(charaHuffmancodePair.get(text[m].charAt(j)));
	 					else if(m==2)
	 					 encoded3.append(charaHuffmancodePair.get(text[m].charAt(j)));
	 						
	 				}
	 				
	 					if(m == 0)
	 					{
	 						 remainingBits[m] = 8 - (encoded1.length() % 8);
	 						 NoofBytes[m] = encoded1.length() / 8;
	 						 //add padding
	 			              for(int k=0;k<remainingBits[m];k++)
	 			            	  encoded1.append(0);       
	 					}
	 					
	 					else if(m == 1)
	 					{
	 						 remainingBits[m] = 8 - (encoded2.length() % 8);
	 						 NoofBytes[m] = encoded2.length() / 8;
	 						 //add padding
	 			              for(int k=0;k<remainingBits[m];k++)
	 			            	  encoded2.append(0);
	 					}
	 					
	 					else if(m == 2)
	 					{
	 						 remainingBits[m] = 8 - (encoded3.length() % 8);
	 						 NoofBytes[m] = encoded3.length() / 8;
	 						 //add padding
	 			              for(int k=0;k<remainingBits[m];k++)
	 			            	  encoded3.append(0);
	 					}
					 
	 					m++;
					 
	 			}

	 			
	 		   StringBuilder totalEncode = new StringBuilder();
	           totalEncode.append(encoded1);
	           totalEncode.append(encoded2);
	           totalEncode.append(encoded3);
		
	           remainingBits[3] = 8 - (totalEncode.length() % 8);	           
	  	       NoofBytes[3]= totalEncode.length() / 8;
	  	       
	  	     //add padding
	              for(int k=0;k<remainingBits[3];k++)
	            	  totalEncode.append(0);
	  			  
	 			 String content ="";
	 			 for(int k=0;k<remainingBits.length;k++) {
	 				content+= NoofBytes[k] +"\n";
		  			content+=remainingBits[k] +"\n";	 
	 			 }
	 		
	  			content+="Header\n";
	              for(Map.Entry code:charaHuffmancodePair.entrySet()) {
	                 content+= ""+  code.getKey() +"-->"+ code.getValue()+"-->"+ freqArray[(code.getKey().toString().charAt(0))] + '\n';
	              }
	              content += "Body\n";
	              Files.writeString(Paths.get(FileName.replace("f","out.txt")),content,StandardOpenOption.CREATE);
	         
	             
	             //compress file
	              writeOut Out = new writeOut(Paths.get(FileName.replace("f","out.txt"))+"");
	              for (int k = 0; k < totalEncode.length(); k++) {
	  				if (totalEncode.charAt(k) == '0')
	  					Out.writeBit(false); //Write 0 to the file
	  				else
	  					Out.writeBit(true); //Write 1 to the file
	  			}
	             Out.close();
	                    
	             //printing each character and its ASCII code and Huffman code
	               System.out.println("character"+"    "+"ASCII"+"          "+"Code");
	                    for(Map.Entry code:charaHuffmancodePair.entrySet()) {
	                    	 char c = code.getKey().toString().charAt(0);	  
	                    	 String ASCII="0"+ Integer.toBinaryString((int) c);
	                      	 System.out.println(code.getKey()+"            "+ASCII+"         "+code.getValue());             
	                     }
	                    
	               File inFile1 = new File(Paths.get(FileName.replace("f","\\f\\a.txt")).toString());
	               File inFile2 = new File(Paths.get(FileName.replace("f","\\f\\b.txt")).toString());
	               File inFile3 = new File(Paths.get(FileName.replace("f","\\f\\c.txt")).toString());   
	               File compressedFile = new File(Paths.get(FileName.replace("f","out.txt")).toString());    
	  			   double comprassionRatio =(1.0*(compressedFile.length()) / ((inFile1.length())+(inFile2.length())+(inFile3.length())) ) * 100;
	  		       System.out.println("The compression ratio is   "+comprassionRatio);
  	
	  	}break;
	  	//end case 3
	  	
	  	case 4:
	  	{  
	          BufferedReader reader = new BufferedReader(new FileReader(FileName));
	          String myCharacters = "";
	          String myCodes =  "";
	          int remainingBits[] = new int[4] ;
		  	  int NoofBytes[] = new int[4] ;
	          int i = 0;   
	          String line;
	          Boolean flag = false ;
	          
	          for(i=0;i<4;i++){
	        	  NoofBytes[i] =Integer.parseInt(reader.readLine()); 
		          remainingBits[i] = Integer.parseInt(reader.readLine()); 	  
	          }
	          line = reader.readLine();
	          while (line != null) {
		              

					 if(Objects.equals(line, "Header"))
		                  flag = true;
		              if(Objects.equals(line, "Body"))
		                  flag = false;
		              
		              //Read Header
		              if(flag)
		              {
		                  String [] tempArray =line.split("-->");
		                  if(tempArray.length == 3)
		                  {
		                	  if (tempArray[0].length() == 0) {
								
								 myCharacters = myCharacters+System.getProperty("line.separator")+"~";
								 myCodes = myCodes+tempArray[1]+"~";
								
	                          	}
		                	  else {
	                          		myCharacters = myCharacters+tempArray[0]+"~";
	                          		myCodes = myCodes+tempArray[1]+"~";
	                          	}	  
		                  }

		              }
		              else
		              {	 
		                	  break;
		              }
		             
					line = reader.readLine();
					
				}//end while
	            reader.close();
	            

	          //create map for characters and their codes
	          String [] charArray = myCharacters.split("~");
	          String [] CodeArray = myCodes.split("~");
	          
	          for(int j=0;j<charArray.length;j++)
	        	  CodeCharacterPair.put(CodeArray[j], charArray[j].charAt(0)); 
	          
	       	 //Reading the body and Decompressing it
	           byte[] File = Files.readAllBytes(Paths.get(FileName));
	  	       int fileSize = File.length;
	           StringBuilder BinaryDecompress = getCompressedString(File, fileSize, NoofBytes[3]);
	           int length = BinaryDecompress.length()-1;
	           StringBuilder BinaryDecompress1 = new StringBuilder();
	           StringBuilder BinaryDecompress2 = new StringBuilder();
	           StringBuilder BinaryDecompress3 = new StringBuilder();
	           
	           
	          	  //remove padding
	          	  BinaryDecompress.delete(length-remainingBits[3],length);
	          	  
	          	  //divide it back into 3 parts 
	          	  int Size1 =  NoofBytes[0]*8 + 8-remainingBits[0];
	          	  int Size2 =  NoofBytes[1]*8 + 8-remainingBits[1];
	          	  int Size3 =  NoofBytes[2]*8 + 8-remainingBits[2];
	          	  
	          	  
	          	  for(i=0;i<Size1+remainingBits[0];i++)
	          	  {
	          		  BinaryDecompress1.append(BinaryDecompress.charAt(i));
	          	  }
	          	  for(i=Size1+remainingBits[0];i<Size1+remainingBits[0]+Size2+remainingBits[1];i++)
	          	  {
	          		  BinaryDecompress2.append(BinaryDecompress.charAt(i));
	          	  }
	          	  for(i=Size1+remainingBits[0]+Size2+remainingBits[1];i<Size1+remainingBits[0]+Size2+remainingBits[1]+Size3+remainingBits[2];i++)
	          	  {
	          		  BinaryDecompress3.append(BinaryDecompress.charAt(i));
	          	  }
	          	  //remove padding
	          	 int length1 = BinaryDecompress1.length()-1;
	          	 int length2 = BinaryDecompress2.length()-1;
	          	 int length3 = BinaryDecompress3.length();
	          	 BinaryDecompress1.delete(length1-remainingBits[0],length1);
	          	 BinaryDecompress2.delete(length2-remainingBits[1],length2);
	          	 BinaryDecompress3.delete(length3-remainingBits[2],length3);
	          	 
	          	 
	          	 
	         
	          	//decoding the decompressed string
	          	 long starttime = System.nanoTime();
	          	 String tempString = "";
	          	 StringBuilder out1 = new StringBuilder();
	          	 StringBuilder out2 = new StringBuilder();
	          	 StringBuilder out3 = new StringBuilder();
	          	  for(i=0;i<BinaryDecompress1.length();i++)
	          	  {
	          		 tempString+=BinaryDecompress1.charAt(i);
	          		 for(Map.Entry code:CodeCharacterPair.entrySet())
	          		 {
	       			    if(code.getKey().equals(tempString))
	       			    {
	       				  check = code.getValue().toString().charAt(0);
	       				  out1.append(check);
	       				  tempString = "";
	       				  break;   				  
	       			    }
	          		 } 
	          	 }
	          	tempString = "";
	          	 for(i=0;i<BinaryDecompress2.length();i++)
	          	  {
	          		 tempString+=BinaryDecompress2.charAt(i);
	          		 for(Map.Entry code:CodeCharacterPair.entrySet())
	          		 {
	       			    if(code.getKey().equals(tempString))
	       			    {
	       				  check = code.getValue().toString().charAt(0);
	       				  out2.append(check);
	       				  tempString = "";
	       				  break;   				  
	       			    }
	          		 } 
	          	 }
	          	tempString = "";
	          	 for(i=0;i<BinaryDecompress3.length();i++)
	          	  {
	          		 tempString+=BinaryDecompress3.charAt(i);
	          		 for(Map.Entry code:CodeCharacterPair.entrySet())
	          		 {
	       			    if(code.getKey().equals(tempString))
	       			    {
	       				  check = code.getValue().toString().charAt(0);
	       				  out3.append(check);
	       				  tempString = "";
	       				  break;   				  
	       			    }
	          		 } 
	          	 }
	          	  
	      
	              long finishtime = System.nanoTime();
		          long time = finishtime - starttime;
		          System.out.println("Time in nanosecond is  "+time);
 	          		 
	          	Files.writeString(Paths.get(FileName.replace("out.txt","\\f\\a.txt")),out1,StandardOpenOption.CREATE);     
	          	Files.writeString(Paths.get(FileName.replace("out.txt","\\f\\b.txt")),out2,StandardOpenOption.CREATE);   
	          	Files.writeString(Paths.get(FileName.replace("out.txt","\\f\\c.txt")),out3,StandardOpenOption.CREATE);   

	  	}break;
	  	//end case 4
	

	  }//end switch
	 	  
	}//end main
	
	private static void recursiveGetHuffmanCode(node node, String code) { //takes root and empty string
	    if (node != null) {
	        if (node.right != null)
	        	recursiveGetHuffmanCode(node.right, code + "1");
	        if (node.left != null)
	        	recursiveGetHuffmanCode(node.left, code + "0");
	        if (node.left == null && node.right == null)
	        	charaHuffmancodePair.put(node.character.charAt(0),code);//fill map with character & Huffman code
	    }
	} 
	private static void BuildTree(PriorityQueue<node> pQueue) {
		while(pQueue.size()>1) 
		{
			node x = pQueue.poll();
			node y = pQueue.poll();
			pQueue.add(new node(x,y));
		}
	}
	
	static StringBuilder getCompressedString(byte[] File, int fileSize, int bytesLength) {

		for (int i = (fileSize - bytesLength)-1; i < fileSize; i++) {
			int temp = (byte) File[i] & 0xFF;
			string.append(String.format("%8s", Integer.toBinaryString(temp)).replace(' ', '0'));
		}
		return string;
}
	
}//end Huffman class

