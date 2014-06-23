package alignmentStudy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test stubs.
 */
public class JaroWinklerTest 
extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public JaroWinklerTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( JaroWinklerTest.class );
	}

	/**
	 * Test stub
	 */
	public void testOne()
	{
		JaroWinkler jw = new JaroWinkler();
                assertEquals(1.0, jw.getJaroWinklerDistance ("JaroWinkler", "JaroWinkler"));
	}

	/**
	 * Test stub
	 */
	public void testTwo()
	{
		JaroWinkler jw = new JaroWinkler();
		assertEquals(0.7527777777777779, jw.getJaroWinklerDistance ("barnes", "anderson"));
	}

	/**
	 * Test stub
	 */
	public void testThree()
	{
		JaroWinkler jw = new JaroWinkler();
		assertEquals(0.9611111111111111, jw.getJaroWinklerDistance ("martha", "marhta"));
	}

	/**
	 * Test stub
	 */
	public void testFour()
	{
		JaroWinkler jw = new JaroWinkler();
		assertEquals(0.8400000000000001, jw.getJaroWinklerDistance ("dwayne", "duane"));
	}

	/**
	 * Test stub
	 */
	public void testFive()
	{
		JaroWinkler jw = new JaroWinkler();
		assertEquals(0.8133333333333332, jw.getJaroWinklerDistance ("dixon", "dicksonx"));
	}
}
