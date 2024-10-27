public class CourseScheduler {
    public static int partition(int[][] arr, int begin, int end) {
        int pivot = arr[end][1];
        int i = begin - 1;

        for (int j = begin; j < end; j++) {
            if (arr[j][1] <= pivot) {
                i++;

                int[] temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        int[] temp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = temp;

        return i + 1;
    }

    public static void quickSortByCourseEndTime(int[][] arr, int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSortByCourseEndTime(arr, begin, partitionIndex - 1);
            quickSortByCourseEndTime(arr, partitionIndex + 1, end);
        }
    }

    public static int maxNonOverlappingCourses(int[][] courses) {
        if (courses.length == 0) {
            return 0;
        }

        int firstCourseIndex = 0;
        int lastCourseIndex = courses.length - 1;

        quickSortByCourseEndTime(courses, firstCourseIndex, lastCourseIndex);

        int maxCourses = 0;
        int lastEndTime = -1;

        for (int[] course : courses) {
            if (course[0] >= lastEndTime) {
                maxCourses++;
                lastEndTime = course[1];
            }
        }

        return maxCourses;
    }
}
