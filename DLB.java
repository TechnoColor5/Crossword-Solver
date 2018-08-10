/**
 * @author Daniel Mailloux
 * @version v1.0
 *
 * This is a DLB version of the myDictionary class provided for the project.
 * Using this class allows for a much faster run-time.
*/

public class DLB implements DictInterface
{
	private char terminator = '*';
	DLBnode root;

	public DLB()
	{
		root = new DLBnode();
	}

	public boolean add(String s)
	{
		s = s + terminator;	 //adds terminator character to each string
		DLBnode currNode = root;
		boolean madeChildren = false;

		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			DLBnodeAnswer result = addChildren(c, currNode);
			currNode = result.node;
			madeChildren = result.value;
		}
		return madeChildren;
	}

	//checks if a child should be added
	//If a child is added, returns true with the new node
	public DLBnodeAnswer addChildren(char c, DLBnode parent)
	{
		//if no children exist, a new one must be made
		if(parent.child == null)
		{
			parent.child = new DLBnode(c);
			return new DLBnodeAnswer(parent.child, true);
		}
		else
		{		//looks for the child with the correct value
			DLBnodeAnswer result = searchCurrentLvl(c, parent.child);
			if(!result.value)	//if the child was not found, make a new one
			{
				result.node.rightSib = new DLBnode(c);	//node wasn't found so make a new one
				result.node = result.node.rightSib;
				result.value = true;
			}
			return result;
		}
	}
	/*Searches the current level for a node whose value matches c
		If none are found, return last child (with null sibling) and return false
	*/
	public DLBnodeAnswer searchCurrentLvl(char c, DLBnode node)
	{
		while (node.rightSib != null)
		{
			if(node.val == c)
				break;
			node = node.rightSib;
		}
		if(node.val == c) 
			return new DLBnodeAnswer(node, true);
		else
			return new DLBnodeAnswer(node, false);	//return false with last node
	}

	//Searches to see if the node has a sibling that IS NOT '*'
	public boolean hasOtherSib(DLBnode peer)
	{
		while(peer.rightSib != null)
		{
			if(peer.val != terminator)		//if it finds 1 other sibling that is not a '*', its a prefix
				return true;
			peer = peer.rightSib;
		}
		if(peer.val != terminator)
			return true;
		return false;
	}

	//Returns whether a stringBuilder is a word, prefix, both, or neither
	public int searchPrefix(StringBuilder s)
	{
		boolean word = false;
		boolean prefix = false;
		DLBnode currNode = root;
		DLBnodeAnswer result;

		//If there's nothing in the stringBuilder, it's not a word
		if (s.length() == 0)
			return 0;
		
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);

			if (currNode.child != null)
				result = searchCurrentLvl(c, currNode.child);	//searches the Children of currNode for c
			else return 0;

			if (result.value)	//if found sets currNode to the node found
			{
				currNode = result.node;
			}
			else return 0;
		}

		if(currNode.val == s.charAt(s.length() - 1))	//if this is true, it found all of the characters in s
		{
			result = searchCurrentLvl(terminator, currNode.child);	//searches for terinator in children
			if (result.value)
				word = true;
			prefix = hasOtherSib(currNode.child);
		}
		
		if (word && prefix) return 3;
		else if (word) return 2;
		else if (prefix) return 1;
		else return 0;
	}

	public int searchPrefix(StringBuilder s, int start, int end)
	{
		boolean word = false;
		boolean prefix = false;
		DLBnode currNode = root;
		DLBnodeAnswer result;

		//If there's nothing in the stringBuilder, it's not a word
		if (s.length() == 0)
			return 0;

		//Starts at start and goes until end
		for (int i = start; i <= end; i++)
		{
			char c = s.charAt(i);

			if (currNode.child != null)
				result = searchCurrentLvl(c, currNode.child);	//searches the Children of currNode for c
			else return 0;

			if (result.value)	//if found sets currNode to the node found
			{
				currNode = result.node;
			}
			else return 0;
		}

		if(currNode.val == s.charAt(s.length() - 1))	//if this is true, it found all of the characters in s
		{
			result = searchCurrentLvl('*', currNode.child);	//searches for terinator in children
			if (result.value)
				word = true;
			prefix = hasOtherSib(currNode.child);
		}
		
		if (word && prefix) return 3;
		else if (word) return 2;
		else if (prefix) return 1;
		else return 0;
	}

	private class DLBnode
	{
		private char val;
		private DLBnode rightSib;
		private DLBnode child;

		public DLBnode() { }

		public DLBnode(char c)
		{
			val = c;
		}

	}

	
	private class DLBnodeAnswer
	{
		private DLBnode node;
		private boolean value;

		public DLBnodeAnswer(DLBnode n, boolean b)
		{
			node = n;
			value = b;
		}
	}

}