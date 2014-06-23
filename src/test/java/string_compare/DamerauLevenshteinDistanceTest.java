package alignmentStudy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test stubs.
 */
public class DamerauLevenshteinDistanceTest 
extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public DamerauLevenshteinDistanceTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( DamerauLevenshteinDistanceTest.class );
	}

	/**
	 * Test stub
	 */
	public void testOne()
	{
		DamerauLevenshteinDistance test = new DamerauLevenshteinDistance();
		assertEquals(1, test.damerauLevenshteinDistance("tuesday", "teusday"));
	}
	
	/**
	 * Test stub
	 */
	public void testTwo()
	{
		DamerauLevenshteinDistance test = new DamerauLevenshteinDistance();
		assertEquals(2, test.damerauLevenshteinDistance("tuesday", "thursday"));
	}
	
	/**
	 * Test stub
	 */
	public void testThree()
	{
		DamerauLevenshteinDistance test = new DamerauLevenshteinDistance();
		assertEquals(8, test.damerauLevenshteinDistance("tuesday", "something"));
	}

	/**
	 * Test stub
	 */
	public void testEqualStrings()
	{	
		DamerauLevenshteinDistance test = new DamerauLevenshteinDistance();
		assertEquals(0, test.damerauLevenshteinDistance("tuesday", "tuesday"));
	}
}
