import static org.junit.Assert.assertEquals;

/**
 * Created by sumsk on 20-Mar-17.
 */
public class KarateChop
{
    /**
     * Pros: relatively simple.
     * Cons: Quite tricky to figure out proper check range values and end goal end condition when index was not found
     * @param searchNum
     * @param searchArray
     * @return the integer index of the target in the array, or -1 if the target is not in the array.
     */
    public int chop1(final int searchNum, final int[] searchArray)
    {
        int result = -1;
        boolean indexWasNotFound = false;

        if(searchArray.length > 0)
        {
            indexWasNotFound = true;
        }

        int checkStart = 0;
        int checkEnd = searchArray.length / 2;

        while(indexWasNotFound)
        {
            final int checkResult = chop1CheckArraySide(searchNum, checkStart, checkEnd, searchArray);

            if(checkResult == result)
            {
                if(checkStart == checkEnd)
                {
                    indexWasNotFound = false;
                }

                //Reduce check range
                checkStart = checkEnd;
                checkEnd = searchArray.length - (checkEnd / 2);
            }
            else
            {
                result = checkResult;
                indexWasNotFound = false;
            }
        }

        return result;
    }

    private int chop1CheckArraySide(final int searchNum, final int startIndex, final int endIndex, final int[] array)
    {
        int result = -1;
        int reducedArrayLength = endIndex + 1;

        if(endIndex >= array.length)
        {
            reducedArrayLength = array.length;
        }

        for(int i = startIndex; i < reducedArrayLength; i++)
        {
            int valueToCheck = array[i];

            if(searchNum == valueToCheck)
            {
                result = i;
                break;
            }
        }

        return result;
    }

    /**
     * Recursive implementation of binary chop.
     * Pros:
     * Cons:
     * @param searchNum
     * @param searchArray
     * @return the integer index of the target in the array, or -1 if the target is not in the array.
     */
    public int chop2(final int searchNum, final int[] searchArray)
    {
        int result = -1;

        return result;
    }

    public static void main(String[] args)
    {
        KarateChop obj = new KarateChop();

        final long startTimeChop1 = System.currentTimeMillis();
        assertEquals(-1, obj.chop1(3, new int[]{}));
        assertEquals(-1, obj.chop1(3, new int[]{1}));
        assertEquals(0,  obj.chop1(1, new int[]{1}));

        assertEquals(0,  obj.chop1(1, new int[]{1, 3, 5}));
        assertEquals(1,  obj.chop1(3, new int[]{1, 3, 5}));
        assertEquals(2,  obj.chop1(5, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop1(0, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop1(2, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop1(4, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop1(6, new int[]{1, 3, 5}));

        assertEquals(0,  obj.chop1(1, new int[]{1, 3, 5, 7}));
        assertEquals(1,  obj.chop1(3, new int[]{1, 3, 5, 7}));
        assertEquals(2,  obj.chop1(5, new int[]{1, 3, 5, 7}));
        assertEquals(3,  obj.chop1(7, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop1(0, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop1(2, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop1(4, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop1(6, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop1(8, new int[]{1, 3, 5, 7}));
        final long endTimeChop1 = System.currentTimeMillis() - startTimeChop1;

        System.out.println("Chop1 tests were completed in: " + endTimeChop1 + " ms");

        final long startTimeChop2 = System.currentTimeMillis();
        assertEquals(-1, obj.chop2(3, new int[]{}));
        assertEquals(-1, obj.chop2(3, new int[]{1}));
        assertEquals(0,  obj.chop2(1, new int[]{1}));

        assertEquals(0,  obj.chop2(1, new int[]{1, 3, 5}));
        assertEquals(1,  obj.chop2(3, new int[]{1, 3, 5}));
        assertEquals(2,  obj.chop2(5, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop2(0, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop2(2, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop2(4, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop2(6, new int[]{1, 3, 5}));

        assertEquals(0,  obj.chop2(1, new int[]{1, 3, 5, 7}));
        assertEquals(1,  obj.chop2(3, new int[]{1, 3, 5, 7}));
        assertEquals(2,  obj.chop2(5, new int[]{1, 3, 5, 7}));
        assertEquals(3,  obj.chop2(7, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop2(0, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop2(2, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop2(4, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop2(6, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop2(8, new int[]{1, 3, 5, 7}));
        final long endTimeChop2 = System.currentTimeMillis() - startTimeChop2;

        System.out.println("Chop2 tests were completed in: " + endTimeChop2 + " ms");
    }
}
