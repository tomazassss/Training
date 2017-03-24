package com.viavi.vsamonitor.tests;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
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

    /**
     * Pros: Should be faster with bigger data
     * Cons: Prone for multithreading issues. Slightly overcomplicated.
     *
     * I like improved division step calculation.
     *
     * @param searchNum
     * @param searchArray
     * @return the integer index of the target in the array, or -1 if the target is not in the array.
     */
    public int chop4(final int searchNum, final int[] searchArray)
    {
        int result = -1;
        int arrayLength = searchArray.length;

        if(arrayLength == 1)
        {
            if(searchArray[0] == searchNum)
            {
                result = 0;
            }
        }
        else if(arrayLength > 0)
        {
            // Split array into as much half sizes as possible. Create threads for each half size and let them rip.
            //As soon as the positive result is returned or all threads have finished we return result.

            List<Integer> steps = chop4CollectSteps(arrayLength);
            List<Integer> results = new ArrayList<>();
            int numberOfSteps = steps.size();

            CountDownLatch threadExecutionLatch = new CountDownLatch(numberOfSteps);
            int startIndex = 0;
            int endIndex = steps.get(0);

           for(int i = 0; i < numberOfSteps; i++)
           {
                final Chop4CheckSide chop4CheckSide = new Chop4CheckSide(threadExecutionLatch);

                if(i > 0)
                {
                    int previousStep = steps.get(i-1);
                    startIndex = previousStep;
                    endIndex = previousStep + steps.get(i);
                }

                chop4CheckSide.setSearchParams(searchNum, searchArray, startIndex, endIndex);
                chop4CheckSide.setResultGatherer(results);
                Thread thread = new Thread(chop4CheckSide);
                thread.start();
           }

            try
            {
                threadExecutionLatch.await(10, TimeUnit.SECONDS);

                if(results.size() > 0)
                {
                    result = results.get(0);
                }

            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }

        return result;
    }

    private synchronized void addResult(final int result, final List<Integer> results)
    {
        results.add(result);
    }

    class Chop4CheckSide implements Runnable
    {
        private final CountDownLatch executionLatch;
        private int searchNum;
        private int[] searchArray;
        private int startIndex;
        private int endIndex;
        private List<Integer> resultGatherer;

        Chop4CheckSide(final CountDownLatch latch)
        {
            this.executionLatch = latch;
        }

        public void setSearchParams(final int searchNum, final int[] searchArray, final int startIndex, final int endIndex)
        {
            this.searchNum = searchNum;
            this.searchArray = searchArray;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public void setResultGatherer(final List<Integer> results)
        {
            this.resultGatherer = results;
        }

        @Override
        public void run()
        {
            for(int i = startIndex; i <= endIndex; i++)
            {
                if(i < searchArray.length && searchArray[i] == searchNum)
                {
                    addResult(i, resultGatherer);
                }
            }

           executionLatch.countDown();
        }
    }

    private List<Integer> chop4CollectSteps(final int arrayLength)
    {
        List<Integer> steps = new ArrayList<>();

        int stepDivider = 2;
        int numberOfItemsCovered = 0;
        boolean notAllStepsCollected = true;

        while(notAllStepsCollected)
        {
            int step = arrayLength / stepDivider;

            if(step > 0)
            {
                steps.add(step);
                stepDivider = stepDivider * 2;
                numberOfItemsCovered += step;
            }
            else if(numberOfItemsCovered < arrayLength)
            {
                steps.add(arrayLength - numberOfItemsCovered);
                notAllStepsCollected = false;
            }
            else
            {
                notAllStepsCollected = false;
            }
        }

        return steps;
    }

    /**
     * Pros:
     * Cons:
     *
     * @param searchNum
     * @param searchArray
     * @return the integer index of the target in the array, or -1 if the target is not in the array.
     */
    public int chop5(final int searchNum, final int[] searchArray)
    {
        int result = -1;
        int arrayLength = searchArray.length;

        if(arrayLength == 1)
        {
            if(searchArray[0] == searchNum)
            {
                result = 0;
            }
        }
        else if(arrayLength > 0)
        {
            //TODO How is this different from first chop?
            //Calculate halve
            //Apply Function<SearchParams,Integer>
            //If found - end
            //else calculate next halve
        }

        return result;
    }

    /**
     * Maybe idea for another implementation
     */
    class ChopProducer implements Supplier<Integer>
    {

        @Override
        public Integer get()
        {
            int result = -1;



            return result;
        }
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

        final long startTimeChop4 = System.currentTimeMillis();
        assertEquals(-1, obj.chop4(3, new int[]{}));
        assertEquals(-1, obj.chop4(3, new int[]{1}));
        assertEquals(0,  obj.chop4(1, new int[]{1}));

        assertEquals(0,  obj.chop4(1, new int[]{1, 3, 5}));
        assertEquals(1,  obj.chop4(3, new int[]{1, 3, 5}));
        assertEquals(2,  obj.chop4(5, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop4(0, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop4(2, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop4(4, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop4(6, new int[]{1, 3, 5}));

        assertEquals(0,  obj.chop4(1, new int[]{1, 3, 5, 7}));
        assertEquals(1,  obj.chop4(3, new int[]{1, 3, 5, 7}));
        assertEquals(2,  obj.chop4(5, new int[]{1, 3, 5, 7}));
        assertEquals(3,  obj.chop4(7, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop4(0, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop4(2, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop4(4, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop4(6, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop4(8, new int[]{1, 3, 5, 7}));
        final long endTimeChop4 = System.currentTimeMillis() - startTimeChop4;

        System.out.println("Chop4 tests were completed in: " + endTimeChop4 + " ms");

        final long startTimeChop5 = System.currentTimeMillis();
        assertEquals(-1, obj.chop5(3, new int[]{}));
        assertEquals(-1, obj.chop5(3, new int[]{1}));
        assertEquals(0,  obj.chop5(1, new int[]{1}));

        assertEquals(0,  obj.chop5(1, new int[]{1, 3, 5}));
        assertEquals(1,  obj.chop5(3, new int[]{1, 3, 5}));
        assertEquals(2,  obj.chop5(5, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop5(0, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop5(2, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop5(4, new int[]{1, 3, 5}));
        assertEquals(-1, obj.chop5(6, new int[]{1, 3, 5}));

        assertEquals(0,  obj.chop5(1, new int[]{1, 3, 5, 7}));
        assertEquals(1,  obj.chop5(3, new int[]{1, 3, 5, 7}));
        assertEquals(2,  obj.chop5(5, new int[]{1, 3, 5, 7}));
        assertEquals(3,  obj.chop5(7, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop5(0, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop5(2, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop5(4, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop5(6, new int[]{1, 3, 5, 7}));
        assertEquals(-1, obj.chop5(8, new int[]{1, 3, 5, 7}));
        final long endTimeChop5 = System.currentTimeMillis() - startTimeChop5;

        System.out.println("Chop5 tests were completed in: " + endTimeChop5 + " ms");

        testWithLargeAmount(obj);
    }

    private static void testWithLargeAmount(final KarateChop obj)
    {
        int[] largeArray = formArray(90000);

        int expectedIndex = 55437;
        int searchNum = largeArray[expectedIndex];

        final long startTimeChop1 = System.currentTimeMillis();
        assertEquals(expectedIndex,  obj.chop1(searchNum, largeArray));
        final long endTimeChop1 = System.currentTimeMillis() - startTimeChop1;

        System.out.println("Large Chop1 tests were completed in: " + endTimeChop1 + " ms");

        final long startTimeChop2 = System.currentTimeMillis();
        assertEquals(expectedIndex,  obj.chop2(searchNum, largeArray));
        final long endTimeChop2 = System.currentTimeMillis() - startTimeChop2;

        System.out.println("Large Chop2 tests were completed in: " + endTimeChop2 + " ms");

        final long startTimeChop3 = System.currentTimeMillis();
        assertEquals(expectedIndex,  obj.chop3(searchNum, largeArray));
        final long endTimeChop3 = System.currentTimeMillis() - startTimeChop3;

        System.out.println("Large Chop3 tests were completed in: " + endTimeChop3 + " ms");

        final long startTimeChop4 = System.currentTimeMillis();
        assertEquals(expectedIndex,  obj.chop4(searchNum, largeArray));
        final long endTimeChop4 = System.currentTimeMillis() - startTimeChop4;

        System.out.println("Large Chop4 tests were completed in: " + endTimeChop4 + " ms");
    }

    private static int[] formArray(int ammount)
    {
        Random rnd = new Random();

        Set<Integer> integerSet = new HashSet<>();

        for(int i = 0; i < ammount; i++)
        {
            boolean elementAdded = false;
            while (!elementAdded)
            {
                final int rndInt = rnd.nextInt(ammount);
                elementAdded = integerSet.add(rndInt);
            }
        }

        final int[] results = integerSet.stream().mapToInt(Number::intValue).toArray();

        return results;
    }
}
