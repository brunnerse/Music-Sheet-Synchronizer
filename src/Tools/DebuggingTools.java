package Tools;

public class DebuggingTools {
    public static void printArray(Object[] array) {
        System.out.print("[ ");
        for (Object o : array) {
            System.out.print(o);
            if (!o.equals(array[array.length - 1]))
                System.out.print(",\t");
        }
        System.out.print(" ]\n");
    }
}
