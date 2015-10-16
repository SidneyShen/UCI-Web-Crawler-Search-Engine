package milestone1;

public class UCI_SearchEngine
{
	public static void main(String[] args)
	{
		Build_Index indexBuilder = new Build_Index();
		try
		{	
			indexBuilder.buildIndex();
			indexBuilder.buildGrams();
		}
		catch(Exception ex)
		{

		}
	}
};