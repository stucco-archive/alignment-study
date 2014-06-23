package alignmentStudy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test stubs.
 */
public class QGramsTest 
extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public QGramsTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( QGramsTest.class );
	}

	/**
	 * Test stub
	 */
	public void testOne()
	{
		QGrams qg = new QGrams();
		assertEquals(0.0, qg.qGramsDistance("today", "today", 2));
	}

	/**
	 * Test stub
	 */
	public void testTwo()
	{
		QGrams qg = new QGrams();
		assertEquals(1.0, qg.qGramsDistance("today", "month", 2));
	}
	
	/**
	 * Test stub
	 */
	public void testThree()
	{
		QGrams qg = new QGrams();
		assertEquals(0.8461538461538461, qg.qGramsDistance("today", "tomorrow", 2));
	}

	/**
	 * Test stub
	 */
	public void testFour()
	{
		QGrams qg = new QGrams();
		assertEquals(0.75, qg.qGramsDistance("tuesday", "monday", 2));
	}

}
