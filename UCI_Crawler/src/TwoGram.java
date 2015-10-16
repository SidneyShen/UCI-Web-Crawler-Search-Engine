class TwoGram
{
	public String Token1;
	public String Token2;

	public TwoGram(String token1, String token2)
	{
		Token1 = token1;
		Token2 = token2;
	}

	@Override public int hashCode() 
	{
		return (Token1 + Token2).hashCode();
	}
	

	@Override public boolean equals(Object aThat) 
	{
	    if ( this == aThat ) 
	    	return true;

	    if ( !(aThat instanceof TwoGram) ) 
	    	return false;
	    
	    TwoGram that = (TwoGram)aThat;

	    return
	      this.Token1.equals(that.Token1) 
	      && 
	      this.Token2.equals(that.Token2);
	}
	
	@Override public String toString()
	{
		return Token1 + "," + Token2;
	}
}