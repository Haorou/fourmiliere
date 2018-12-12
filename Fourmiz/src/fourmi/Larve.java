package fourmi;

public class Larve
{
	private int age = 0;
	private int finNymphose = 1;
	
	public void pupaison() { this.age++; }
	
	public boolean nymphoseEstFinie() 
	{ 
		boolean nymphePrete = false; 
		if(this.age >= finNymphose)
		{
			nymphePrete = true;
		}
		return nymphePrete;
	}
}
