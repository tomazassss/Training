package com.viavi.vsamonitor.tests;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
     * Pros: Much faster.
     * Cons: Harder to maintain and understand.
     *
     * Was easier to write. Reused a lot of knowledge form previous implementation bot directly and indirectly.
     * Added few tweaks I did not thought about in the previous implementation.
     * Still don't feel like it's bugless, easiest to understand or most efficient solution.
     *
     * @param searchNum
     * @param searchArray
     * @return the integer index of the target in the array, or -1 if the target is not in the array.
     */
    public int chop2(final int searchNum, final int[] searchArray)
    {
        int result = -1;

        if(searchArray.length == 1 && searchArray[0] == searchNum)
        {
            result = 0;
        }
        else if(searchArray.length != 0)
        {
            result = chop2Recursive(searchNum, 0, searchArray.length, searchArray);
        }

        return result;
    }

    private int chop2Recursive(final int searchNum, final int startIndex, final int endIndex, final int[] searchArray)
    {
        int result = -1;

        if(startIndex == 0 && endIndex == searchArray.length)
        {
            int reducedEndIndex = endIndex / 2;
            result = chop2CheckSide(searchNum, startIndex, reducedEndIndex, searchArray);

            if(result == -1)
            {
                result = chop2Recursive(searchNum, startIndex, reducedEndIndex, searchArray);
            }

        }
        else if(startIndex != endIndex)
        {
            result = chop2CheckSide(searchNum, startIndex, endIndex, searchArray);

            if(result == -1)
            {
                int reducedStartIndex = endIndex;
                int reducedEndIndex = searchArray.length - (endIndex / 2);

                result = chop2Recursive(searchNum, reducedStartIndex, reducedEndIndex, searchArray);
            }
        }

        return result;
    }

    private int chop2CheckSide(final int searchNum, final int startIndex, final int endIndex, final int[] searchArray)
    {
        int result = -1;
        int reducedArrayLength = endIndex + 1;

        if(endIndex >= searchArray.length)
        {
            reducedArrayLength = searchArray.length;
        }


        for(int i = startIndex; i < reducedArrayLength; i++)
        {
            if(searchArray[i] == searchNum)
            {
                result = i;
                break;
            }
        }

        return result;
    }

    /**
     * Pros: Concise. Quite verbose.
     * Cons: Slow.
     *
     * I think this iteration could be made more concise without loosing verbosity.
     * I like this iteration because of it's self documenting code.
     * I feel like index calculation could be completely rethought, because I'm reusing same one from previous iterations
     *  and I'm not really sure if it's the best.
     *
     * @param searchNum
     * @param searchArray
     * @return the integer index of the target in the array, or -1 if the target is not in the array.
     */
    public int chop3(final int searchNum, final int[] searchArray)
    {
        int result = -1;

        if(searchArray.length > 0)
        {
            final List<Integer> integers = IntStream.of(searchArray).boxed().collect(Collectors.toList());

            //Check first side
            int middleIndex = (integers.size() - 1 ) /2;
            result = integers.subList(0, middleIndex).indexOf(searchNum);

            if(result == -1)
            {
                int startIndex = middleIndex;
                middleIndex = integers.size() - (middleIndex / 2);

                while(result == -1 && startIndex != middleIndex && startIndex < middleIndex)
                {
                    result = integers.subList(startIndex, middleIndex).indexOf(searchNum);

                    if(result != -1)
                    {
                        result = result + startIndex;
                    }

                    startIndex = middleIndex;
                    middleIndex = integers.size() - (middleIndex / 2);
                }
            }
        }

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

        final long startTimeChop3 = System.currentTimeMillis();
        assertEquals(-1, obj.chop3(3, new int[]{}));
        assertEquals(-1, obj.chop3(3, new int[]{1}));
        assertEquals(0,  obj.chop3(1, new int[]{1}));

        assertEquals(0,  obj.chop3(1, new int[]{1, 3, 5}));
        assertEquals(1,  obj.chop3(3, new int[]{1, 3, 5}));
        assertEquals(2,  obj.chop3(5, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop3(0, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop3(2, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop3(4, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop3(6, new int[]{1, 3, 5}));

        assertEquals(0,  obj.chop3(1, new int[]{1, 3, 5, 7}));
        assertEquals(1,  obj.chop3(3, new int[]{1, 3, 5, 7}));
        assertEquals(2,  obj.chop3(5, new int[]{1, 3, 5, 7}));
        assertEquals(3,  obj.chop3(7, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop3(0, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop3(2, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop3(4, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop3(6, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop3(8, new int[]{1, 3, 5, 7}));
        final long endTimeChop3 = System.currentTimeMillis() - startTimeChop3;

        System.out.println("Chop3 tests were completed in: " + endTimeChop3 + " ms");
    }
}
