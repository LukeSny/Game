package something.battleScene;
import java.io.*;
import java.util.ArrayList;
import something.PlayerModel;
public class ATSort {

        // A utility function to swap two elements
        static void swap(ArrayList<PlayerModel> arr, int i, int j)
        {
            double temp = arr.get(i).ATWeight;
            arr.get(i).ATWeight = arr.get(j).ATWeight;
            arr.get(j).ATWeight = temp;
        }

        /* This function takes last element as pivot, places
           the pivot element at its correct position in sorted
           array, and places all smaller (smaller than pivot)
           to left of pivot and all greater elements to right
           of pivot */
        static int partition(ArrayList<PlayerModel> arr, int low, int high)
        {

            // pivot
            double pivot = arr.get(high).ATWeight;

            // Index of smaller element and
            // indicates the right position
            // of pivot found so far
            int i = (low - 1);

            for(int j = low; j < high - 1; j++)
            {

                // If current element is smaller
                // than the pivot
                if (arr.get(j).ATWeight <= pivot)
                {

                    // Increment index of
                    // smaller element
                    i++;
                    swap(arr, i, j);
                }
            }
            swap(arr, i + 1, high);
            return (i + 1);
        }

        /* The main function that implements QuickSort
                  arr[] --> Array to be sorted,
                  low --> Starting index,
                  high --> Ending index
         */
        static void quickSort(ArrayList<PlayerModel> arr, int low, int high)
        {
            if (low < high)
            {

                // pi is partitioning index, arr[p]
                // is now at right place
                int pi = partition(arr, low, high);

                // Separately sort elements before
                // partition and after partition
                quickSort(arr, low, pi - 1);
                quickSort(arr, pi + 1, high);
            }
        }


    }

