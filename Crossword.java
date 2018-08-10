/* CrossWord solving program by Daniel Mailloux (DAM255)
	* Last updated June 7th, 2018
*/

import java.io.*;
import java.util.*;

public class Crossword
{
	public static DictInterface D;
	public static StringBuilder[] words;
	public static ArrayList<ArrayList<Integer>> solidBlock;
	public static int solutionCt;
	public static String dictType;

	public static void main(String [] args) throws IOException
	{
		dictType = args[0];
		Scanner fileScan = new Scanner(new FileInputStream(args[1]));
		int size = Integer.parseInt( fileScan.nextLine() );
		char board[][]=new char[size][size];
		words = new StringBuilder[size * 2];
		solidBlock = new ArrayList<ArrayList<Integer>>(size * 2);
		solutionCt = 0;

		if (dictType.equals("DLB"))
			D = new DLB();
		else
			D = new MyDictionary();

		for(int i = 0; i < words.length; i++)	//initializes all of the StringBuilders
		{
			words[i] = new StringBuilder("");
			solidBlock.add(new ArrayList<Integer>());
		}

		initDictionary();

		for(int r=0; r<size; r++)	//fills starting board
		{
			String str = fileScan.next();
			for(int c=0; c<size; c++)
			{
				board[r][c]=str.charAt(c);
			}
		}

		System.out.println("Here is the board:");
		System.out.println();
		outputBoard(board, size);

		boolean found = findSolution(0, 0, board, size);
		if(!dictType.equals("DLB"))
		{
			if(found)
				System.out.println("Soultion found!");
			else System.out.println("No solution found!");
			outputBoard(board, size);
		}
		else
		{
			System.out.println("Solutions found: "+solutionCt);
		}
	}

	public static boolean findSolution(int r, int c, char[][] board, int size)
	{


		if (r == size)	//if r == size, you have finished
		{
			if(dictType.equals("DLB"))
			{
				solutionCt++;
				if(solutionCt % 10000 == 0 || solutionCt == 1)
				{
					System.out.println("Solutions so far: "+solutionCt);
					outputBoard(board, size);
				}
				return false;
			}
			else
				return true;
		}

		char alpha[] = {
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
		};
		boolean result = false;
		boolean endOfWord = false;
		boolean endVert = false;
		boolean endHoriz = false;
		int horizStat = 0;	//used to hold status of horziStr
		int vertStat = 0;		//used to hold statuss of vertStr

		StringBuilder vertStr = words[c];						//gets the particular vertical and horizontal strings for the
		StringBuilder horizStr = words[r + size];		//current r and c

		ArrayList<Integer> vertSolid = solidBlock.get(c);					//these are used to hold the positions
		ArrayList<Integer> horizSolid = solidBlock.get(r + size);	//of the '-'s in the strings

		//testing ends
		if(r == size-1)
			endVert = true;
		else if(board[r+1][c] == '-')
			endVert = true;

		if(c == size-1)
			endHoriz = true;
		else if(board[r][c+1] == '-')
			endHoriz = true;


		if (board[r][c] == '-')
		{
			vertSolid.add(r);				//adds position of '-' at place r for the vertical stringbuilder
			horizSolid.add(c);			//same as above but for the horizontal StringBuilder
			vertStr.append(Character.toLowerCase(board[r][c]));				//adds current spot to vertical Sting
			horizStr.append(Character.toLowerCase(board[r][c]));			//adds current spot to horizontal Sting
			if( c == size - 1)
				result = findSolution(r+1, 0, board, size);			//goes to next row
			else 
				result = findSolution(r, c+1, board, size);			//goes to next column
			if(!result)		//if it comes back false, need to delete '-' from strings and solid ArrayLists
			{
				vertSolid.remove(vertSolid.size()-1);
				horizSolid.remove(horizSolid.size()-1);
				vertStr.deleteCharAt(vertStr.length() - 1);
				horizStr.deleteCharAt(horizStr.length() - 1);
			}

		}
		else if(board[r][c] != '+')		//if character already on board
		{
			if(r == size - 1 && c == size - 1)		//This is to get the program to finish correctly
			{
				result = findSolution(r+1, 0, board, size);			//goes to next row
			}

			//The if statements are to make up for adding it in the for loop below
			if(r == vertStr.length() || vertStr.length() == 0)
				vertStr.append(Character.toLowerCase(board[r][c]));				//adds current spot to vertical Sting
			if(c == horizStr.length() || horizStr.length() == 0)
				horizStr.append(Character.toLowerCase(board[r][c]));			//adds current spot to horizontal Sting

			//if end of row
			if(c == size - 1)
			{
				horizStat = getCurrStatus(c, horizStr, horizSolid);
				vertStat = getCurrStatus(r, vertStr, vertSolid);

				//check ends, if not a word return false
				if((endVert && vertStat == 1) || (endHoriz && horizStat == 1) || (vertStat == 0 || horizStat == 0))
					return false;
				result = findSolution(r+1, 0, board, size);			//goes to next row
			}
			else
			{
				result = findSolution(r, c+1, board, size);			//goes to next column
				if(result == false)		//delete characters if not a word
				{
					while (vertStr.length() != r)
						vertStr.deleteCharAt(vertStr.length()-1);
					while (horizStr.length() != c)
						horizStr.deleteCharAt(horizStr.length()-1);
				}
			}				
		}
		else
		{
			for (int alphaCt = 0; alphaCt < 26; alphaCt++)
			{
				char let = alpha[alphaCt];		//grabs the next letter from the alpha array
				board[r][c] = let;		//places letter on board
				vertStr.append(Character.toLowerCase(let));			//add letter to stringBuilder
				horizStr.append(Character.toLowerCase(let));		//add letter to stringBuilder


				//check if next spot has a character
				boolean nextToLetH = false;
				boolean nextToLetV = false;

				if (c != size - 1 )	//horizontal
				{
					if (board[r][c + 1] != '+' && board[r][c + 1] != '-')
					{
						nextToLetH = true;
						horizStr.append(board[r][c + 1]);
						if(c != size -2)
						{
							if(board[r][c+2] == '-')
								endHoriz = true;
						}
					}
				}
				if(r != size - 1)	//vertical
				{
					if(board[r + 1][c] != '+' && board[r + 1][c] != '-')
					{
						nextToLetV = true;
						vertStr.append(board[r + 1][c]);
						if(r != size -2)
						{
							if(board[r+2][c] == '-')
								endVert = true;
						}
					}
				}

				horizStat = getCurrStatus(c, horizStr, horizSolid);
				vertStat = getCurrStatus(r, vertStr, vertSolid);

				boolean goodWord = true;
				//If we are at and end and not a word, it is not a good word
				if((endVert && vertStat == 1) || (endHoriz && horizStat == 1) || (vertStat == 0 || horizStat == 0))
					goodWord = false;
				if(goodWord)	//goes to next spot on board
				{
					if (c == size - 1)
						result = findSolution(r+1, 0 , board, size); //goes to next row
					else result = findSolution(r, c+1, board, size);		//goes to next column
				}					

				if(nextToLetV)
					vertStr.deleteCharAt(vertStr.length()-1);
				if(nextToLetH)
					horizStr.deleteCharAt(horizStr.length()-1);

				if (result)
					break;
				//if this spot is next to a letter preplaced on the board, need to delete BOTH letters from strVert and strHoriz

				//Since result is false, we need to backtrack
				if (vertStr.length() != r)
					vertStr.deleteCharAt(vertStr.length() - 1);
				if(horizStr.length() != c)
					horizStr.deleteCharAt(horizStr.length() - 1);
				if(alphaCt == 25)
					board[r][c] = '+';	//places a '+' back on the board
			}
		}
		return result;
	}



	//Takes in either the r or c (as x) and the corresponding stringBuilder
	//Returns the status of that StringBuilder
	public static int getCurrStatus(int x, StringBuilder str, ArrayList<Integer> solids)
	{
		int closestSolid = getClosestEnd(x, solids);
		int status = 0;
		if (str.toString().equals("-"))	//if only character in str is '-', return 3 (this is an edge case)
			return 3;
		if (closestSolid == -1)
		{
			status = D.searchPrefix(str);
		} else if (closestSolid == x)
			{
				status = D.searchPrefix(str, 0, closestSolid - 1);
			} else
				{
					status = D.searchPrefix(str, closestSolid + 1, str.length()-1);
				}

		return status;
	}

	//iterates through solids arrayList looking for the closest solid to the current position
	public static int getClosestEnd(int x, ArrayList<Integer> solid)
	{
		int closest = -1;
		for(int i = 0; i < solid.size(); i++)		//Finds the closest '-' if one exists
		{
			if(x < solid.get(i))
			break;
			closest = solid.get(i);
		}
		return closest;
	}

	//outputs the board
	public static void outputBoard(char b[][], int size)
	{
		for(int r=0; r<size; r++)
		{
			for(int c=0; c<size; c++)
			{
				System.out.print(b[r][c]+"  ");
			}
			System.out.println();
			System.out.println();
		}

		System.out.println();
		System.out.println("----------------------------------");
		System.out.println();
	}

	//initializes the dictionary
	public static void initDictionary() throws IOException
	{
		Scanner fileScan = new Scanner(new FileInputStream("dict8.txt"));
		String st;
		while (fileScan.hasNext())
		{
			st = fileScan.nextLine();
			D.add(st);
		}
	}
}