import functions.*;
import functions.basic.*;
import functions.meta.Power;
import java.io.*;
import functions.basic.IdentityFunction;

public class Main {

    public static void main(String[] args) {
        
        // Задание 8, 1: sin и cos
        System.out.println("Задание 8.1: sin и cos");
        Function sin = new Sin();
        Function cos = new Cos();
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f, sin(x)=%.4f, cos(x)=%.4f\n", x, sin.getFunctionValue(x), cos.getFunctionValue(x));
        }

        // Задание 8, 2: Табулирование sin и cos
        System.out.println("\nЗадание 8.2: Табулирование sin и cos (5 точек) ");
        TabulatedFunction tabSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f, tabSin(x)=%.4f, tabCos(x)=%.4f\n", x, tabSin.getFunctionValue(x), tabCos.getFunctionValue(x));
        }

        // Задание 8, п.3: Сумма квадратов
        System.out.println("\nRj Задание 8.3: Сумма квадратов (sin^2 + cos^2)");
        Function sinSqr = Functions.power(tabSin, 2);
        Function cosSqr = Functions.power(tabCos, 2);
        Function sumSqr = Functions.sum(sinSqr, cosSqr);
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f, sin^2+cos^2=%.4f\n", x, sumSqr.getFunctionValue(x));
        }

        // Задание 8, п.4: Тест - текстовый поток
        System.out.println("\nЗадание 8.4: Тест write/read (текстовый файл) ");
        try {
            TabulatedFunction tabExp = TabulatedFunctions.tabulate(new Exp(), 0, 10, 11);
            
            // Запись в файл
            Writer out = new FileWriter("Табулированная-exp.txt");
            TabulatedFunctions.writeTabulatedFunction(tabExp, out);
            out.close();
            
            // Чтение из файла
            Reader in = new FileReader("Табулированная-exp.txt");
            TabulatedFunction readExp = TabulatedFunctions.readTabulatedFunction(in);
            in.close();

            // Сравнение
            System.out.println("Сравнение исходной и считанной с файла функции:");
            for (double x = 0; x <= 10; x += 1) {
                System.out.printf("x=%.2f, Orig(x)=%.6f, Read(x)=%.6f\n", x, tabExp.getFunctionValue(x), readExp.getFunctionValue(x));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Задание 8, п.5: Тест - байтовый поток
        System.out.println("\nЗадание 8.5: Тест output/input");
        try {
            TabulatedFunction tabLog = TabulatedFunctions.tabulate(new Log(Math.E), 1, 10, 10);
            
            // Запись в файл
            OutputStream out = new FileOutputStream("Табулированный-log.dat");
            TabulatedFunctions.outputTabulatedFunction(tabLog, out);
            out.close();
            
            // Чтение из файла
            InputStream in = new FileInputStream("Табулированный-log.dat");
            TabulatedFunction readLog = TabulatedFunctions.inputTabulatedFunction(in);
            in.close();

            // Сравнение
            System.out.println("Сравнение исходной и считанной c файла функции:");
            for (double x = 1; x <= 10; x += 1) {
                System.out.printf("x=%.2f, Orig(x)=%.6f, Read(x)=%.6f\n", x, tabLog.getFunctionValue(x), readLog.getFunctionValue(x));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Задание 9: Тест Сериализации (Serializable)
        System.out.println("\nЗадание 9: Тест Сериализации (Сериализация, ArrayTabulatedFunction)");
        try {
            // log(exp(x)) = x
            Function f = Functions.composition(new Log(Math.E), new Exp());
            // Используем ArrayTabulatedFunction для теста Serializable
            TabulatedFunction tabFunc = TabulatedFunctions.tabulate(f, 0, 10, 11); 
            
            // Сериализация (запись объекта)
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Сериализованный массив"));
            out.writeObject(tabFunc);
            out.close();

            // Десериализация (чтение объекта)
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("Сериализованный массив"));
            TabulatedFunction readFunc = (TabulatedFunction) in.readObject();
            in.close();

            // Сравнение
            System.out.println("Сравнение исходной и десериализованной функции: ");
            for (double x = 0; x <= 10; x += 1) {
                System.out.printf("x=%.2f, Orig(x)=%.6f, Read(x)=%.6f\n", x, tabFunc.getFunctionValue(x), readFunc.getFunctionValue(x));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Задание 9 - Externalizable
        System.out.println("\nЗадание 9: Тест Сериализации - Экстернализация, LinkedListTabulatedFunction");
        try {
            // Создаем функцию f(x) = x^2
            Function sqr = new Power(new IdentityFunction(), 2);
            
            // Создаем табулированную функцию на основе LinkedListTabulatedFunction
            double[] values = {0, 1, 4, 9, 16};
            TabulatedFunction tabFuncLinked = new LinkedListTabulatedFunction(0, 4, values);
            
            System.out.println("Исходная функция (LinkedList):");
            for (int i = 0; i < tabFuncLinked.getPointsCount(); i++) {
                 System.out.printf("Point %d: (%.1f, %.1f)\n", i, tabFuncLinked.getPointX(i), tabFuncLinked.getPointY(i));
            }

            // Сериализация
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Сериализованный-linkedlist.ser"));
            out.writeObject(tabFuncLinked);
            out.close();

            // Десериализация
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("Сериализованный-linkedlist.ser"));
            TabulatedFunction readFuncLinked = (TabulatedFunction) in.readObject();
            in.close();

            // Сравнение
            System.out.println("Сравнение исходной и десериализованной функции (LinkedList):");
            for (double x = 0; x <= 4; x += 0.5) {
                System.out.printf("x=%.1f, Orig(x)=%.2f, Read(x)=%.2f\n", x, tabFuncLinked.getFunctionValue(x), readFuncLinked.getFunctionValue(x));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Добавил отдельный блок
        System.out.println("\nДоп. тест Externalizable: LinkedList с ln(exp(x))");
        try {
            java.util.LinkedList<Double> list = new java.util.LinkedList<>();
            for (double x = -5.0; x <= 5.0; x += 0.5) {
                list.add(Math.log(Math.exp(x)));
            }

            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("list-ln-exp.ser"))) {
                out.writeObject(list);
            }

            java.util.LinkedList<Double> readList;
            try (ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream("list-ln-exp.ser"))) {
                readList = (java.util.LinkedList<Double>) in.readObject();
            }

            double x = -5.0;
            for (Double y : readList) {
                System.out.printf("x = %.6f, ln(exp(x)) = %.6f%n", x, y);
                x += 0.5;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
