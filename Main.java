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
        System.out.println("\nЗадание 8.2: Табулирование sin и cos, 5 точек ");
        TabulatedFunction sintab = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction costab = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f, sintab(x)=%.4f, costab(x)=%.4f\n", x, sintab.getFunctionValue(x), costab.getFunctionValue(x));
        }

        // Задание 8, п.3: Сумма квадратов
        System.out.println("\nRj Задание 8.3: Сумма квадратов: sin^2 + cos^2");
        Function sinSqr = Functions.power(sintab, 2);
        Function cosSqr = Functions.power(costab, 2);
        Function sumSqr = Functions.sum(sinSqr, cosSqr);
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("x=%.1f, sin^2+cos^2=%.4f\n", x, sumSqr.getFunctionValue(x));
        }

        // Задание 8, п.4: Тест - текстовый поток
        System.out.println("\nЗадание 8.4: Тест write/read, текстовый файл ");
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
            TabulatedFunction tabLog = TabulatedFunctions.tabulate(new Log(Math.E), 0, 10, 10);
            
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
        System.out.println("\nЗадание 9: Тест Сериализации, ArrayTabulatedFunction");
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
            System.out.println("Сравнение исходной и десериализованной функции LinkedList:");
            for (double x = 0; x <= 4; x += 0.5) {
                System.out.printf("x=%.1f, Orig(x)=%.2f, Read(x)=%.2f\n", x, tabFuncLinked.getFunctionValue(x), readFuncLinked.getFunctionValue(x));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("\nТест Externalizable: LinkedListTabulatedFunction с ln(exp(x))");
        try {
            // Создает точки для ln(exp(x)) = x на [-5, 5] с шагом 0.5
            FunctionPoint[] points = new FunctionPoint[21];
            for (int i = 0; i < 21; i++) {
                double x = -5.0 + i * 0.5;
                points[i] = new FunctionPoint(x, Math.log(Math.exp(x)));  // = x
            }

            LinkedListTabulatedFunction tabLnExp = new LinkedListTabulatedFunction(points);

            System.out.println("Исходный LinkedListTabulatedFunction:");
            for (int i = 0; i < tabLnExp.getPointsCount(); i++) {
                System.out.printf("Point %d: (%.1f, %.1f)\n",
                        i, tabLnExp.getPointX(i), tabLnExp.getPointY(i));
            }

            // Externalizable сериализация функции
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("linkedlist-ln-exp.ser"))) {
                out.writeObject(tabLnExp);  // ← НАША!
            }

            // Десериализация функции
            LinkedListTabulatedFunction readTabLnExp;
            try (ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream("linkedlist-ln-exp.ser"))) {
                readTabLnExp = (LinkedListTabulatedFunction) in.readObject();  // ← НАША!
            }

            // Проверка ln(exp(x)) = x
            System.out.println("\nСравнение до и после сериализации:");
            for (double x = -5.0; x <= 5.0; x += 1.0) {
                double orig = tabLnExp.getFunctionValue(x);
                double read = readTabLnExp.getFunctionValue(x);
                System.out.printf("x=%.1f, Orig=%.6f, Read=%.6f\n",
                        x, orig, read);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
