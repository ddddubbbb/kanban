import java.util.Scanner;

import Tasks.Epic;
import Tasks.SimpleTask;
import Tasks.SubEpic;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner scan = new Scanner(System.in);

        while (true) {
            String noCommand = "Такой команды нет, поробуйте снова.";
            String noId = "с таким ID Отсутствует! Пробуйте снова.";
            String empty = "Список задач пауст.";
            String symbols = "Обозначения статусов в задачах: " + "\n" + "   null = новая" + "\n" +
                    "   true = в процессе" + "\n" + "   false = завершена";

            printMenu();
            int command = scan.nextInt();

            if (command == 1) {
                if (manager.checkForEmpty()) {
                    System.out.println(empty);

                } else {
                    printSubMenu();

                    int subCommand = scan.nextInt();
                    if (subCommand == 1) {
                        if (manager.simpleTaskIds.isEmpty()) {
                            System.out.println(empty);
                        } else {
                            System.out.println(manager.simpleTasks);
                            System.out.println(symbols);
                        }
                    } else if (subCommand == 2) {
                        if (manager.epics.isEmpty()) {
                            System.out.println(empty);
                        } else {
                            System.out.println(manager.epics);
                            System.out.println(symbols);
                        }
                    } else if (subCommand == 3) {
                        if (manager.subEpics.isEmpty()) {
                            System.out.println(empty);
                        } else {
                            System.out.println(manager.subEpics);
                            System.out.println(symbols);
                        }
                    } else {
                        System.out.println(noCommand);
                    }
                }
            } else if (command == 2) {
                if (manager.checkForEmpty()) {
                    System.out.println(empty);
                } else {
                    System.out.println("Список всех доступных ID: " + manager.allIds);
                    System.out.println("Введите ID задачи: ");
                    int scanID = scan.nextInt();
                    if (manager.simpleTasks.containsKey(scanID)) {
                        System.out.println(manager.simpleTasks.get(scanID));
                        System.out.println(symbols);
                    } else if (manager.epics.containsKey(scanID)) {
                        System.out.println(manager.epics.get(scanID));
                        System.out.println(symbols);
                    } else if (manager.subEpics.containsKey(scanID)) {
                        System.out.println(manager.subEpics.get(scanID));
                        System.out.println(symbols);
                    } else {
                        System.out.println("Задача " + noId);
                    }
                }

            } else if (command == 3) {
                if (manager.epics.isEmpty()) {
                    System.out.println(empty);
                } else {
                    System.out.println("Список всех Epic'ов по ID: " + manager.epicIds);
                    System.out.println("Введите ID задачи (Epic)");
                    int epicID = scan.nextInt();
                    if (manager.epics.containsKey(epicID)) {
                        System.out.println(manager.epics.get(epicID).getSubEpicIds());
                        System.out.println("        Для задачи : ");
                        System.out.println(manager.epics.get(epicID));
                        System.out.println("    Список подзадач: ");
                        for (Integer i : manager.epics.get(epicID).getSubEpicIds()) {
                            System.out.println("    " + manager.subEpics.get(i));
                        }
                        System.out.println(symbols);
                    } else {
                        System.out.println("Задача " + noId);
                    }
                }

            } else if (command == 4) {
                System.out.println("Какую задачу хотите создать?");
                printSubMenu();
                int subCommand = scan.nextInt();
                if (subCommand == 1) {
                    System.out.println(" Для нового Simple Task'а ");
                    System.out.println("Введите название: ");
                    scan.nextLine();
                    String title = scan.nextLine();
                    System.out.println("Введите описание: ");
                    String description = scan.nextLine();
                    SimpleTask newSimpleTask = new SimpleTask(title, 0, description, null);
                    System.out.println("Simple Task с ID '" + manager.createSimpleTask(newSimpleTask) + "' создан!");
                } else if (subCommand == 2) {
                    System.out.println(" Для нового Epic'а ");
                    System.out.println("Введите название: ");
                    scan.nextLine();
                    String title = scan.nextLine();
                    System.out.println("Введите описание: ");
                    String description = scan.nextLine();
                    Epic newEpic = new Epic(title, 0, description, null);
                    System.out.println("Sub Epic с ID '" + manager.createEpic(newEpic) + "' создан!");
                } else if (subCommand == 3) {
                    if (manager.epics.isEmpty()) {
                        System.out.println("Для создания SubEpic'а нужен Epic.");
                        System.out.println("Сначала создайте Epic!");
                    } else {
                        System.out.println("К какому Epic'у будет отнесен ваш SubEpic?");
                        System.out.println("Введите ID Epic'a: ");
                        System.out.println("Список доступных ID: " + manager.epicIds);
                        int epicId = scan.nextInt();

                        System.out.println("Введите название SubEpic'а: ");
                        scan.nextLine();
                        String title = scan.nextLine();
                        System.out.println("Введите описание: ");
                        String description = scan.nextLine();
                        SubEpic newSubEpic = new SubEpic(title, 0, description, epicId, null);
                        System.out.println("SubEpic с ID '" + manager.createSubEpic(newSubEpic) + "' создан!");
                    }
                } else {
                    System.out.println(noCommand);
                }

            } else if (command == 5) {
                System.out.println("Какую задачу хотите обновить?");
                printSubMenu();
                int subCommand = scan.nextInt();
                if (subCommand == 1) {
                    System.out.println(" Для обновления Simple Task'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + manager.simpleTaskIds);
                    int id = scan.nextInt();
                    if (manager.simpleTasks.containsKey(id)) {
                        System.out.println("Обновите название: ");
                        scan.nextLine();
                        String title = scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        System.out.println("Введите статус в формате: ");
                        System.out.println("для 'НОВОГО' - null, для 'В ПРОЦЕССЕ' - true, для 'ЗАВЕРШЕНО' - false");
                        Boolean status = scan.nextBoolean();
                        SimpleTask newSimpleTask = new SimpleTask(title, id, description, status);
                        manager.updateSimpleTask(newSimpleTask);
                        System.out.println("Simple Task обновлен!");
                    } else {
                        System.out.println("Simple Task " + noId);
                    }
                } else if (subCommand == 2) {
                    System.out.println(" Для обновления Epica'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + manager.epicIds);
                    int id = scan.nextInt();
                    if (manager.epics.containsKey(id)) {
                        System.out.println("Обновите название: ");
                        scan.nextLine();
                        String title = scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        Epic newEpic = new Epic(title, id, description, null);//Как получить статус из апдейт субепик???
                        manager.updateEpic(newEpic);
                        System.out.println("Epic обновлен!");
                    } else {
                        System.out.println("Epic " + noId);
                    }
                } else if (subCommand == 3) {
                    System.out.println(" Для обновления SubEpica'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + manager.subEpicIds);
                    int id = scan.nextInt();
                    if (manager.subEpics.containsKey(id)) {
                        System.out.println("Обновите название: ");
                        scan.nextLine();
                        String title = scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();

                        System.out.println("Обновите статус в формате: ");
                        System.out.println("для 'НОВОГО' - null, для 'В ПРОЦЕССЕ' - true, для 'ЗАВЕРШЕНО' - false");
                        Boolean status = scan.nextBoolean();
                        int epicId = manager.getSubIdById(id);
                        SubEpic newSubEpic = new SubEpic(title, id, description, epicId, status);
                        manager.updateSubEpic(newSubEpic);
                        System.out.println("SubEpic обновлен!");
                    } else {
                        System.out.println("SubEpic " + noId);
                    }
                } else {
                    System.out.println(noCommand);
                }

            } else if (command == 6) {
                if (manager.checkForEmpty()) {
                    System.out.println(empty);
                } else {
                    System.out.println("Список всех доступных ID: " + manager.allIds);
                    System.out.println("Чтобы узнать статус задачи - введите ID: ");
                    int id = scan.nextInt();
                    System.out.println(symbols);
                    System.out.println();
                    System.out.println("Статус задачи " + id + " = ");
                    manager.getStatusByIds(id);
                }

            } else if (command == 7) {
                if (manager.checkForEmpty()) {
                    System.out.println(empty);
                } else {
                    System.out.println("Список всех доступных ID: " + manager.allIds);
                    System.out.println("Если вы удаляете Epic, в нем будут удалены все SubEpic'и!!!");
                    System.out.println("Чтобы удалить задачу - введите её ID: ");

                    int id = scan.nextInt();
                    if (manager.allIds.contains(id)) {
                        manager.removeForId(id);
                    } else {
                        System.out.println("Задача " + noId);
                    }
                }

            } else if (command == 8) {
                if (manager.allIds.isEmpty()) {
                    System.out.println(empty);
                } else {
                    System.out.println("Вы уверены, что хотите удалить ВСЕ задачи?");
                    System.out.println("Введите символ '1', если хотите продолжить");
                    System.out.println("Любой другой символ, чтобы выйти в главное меню");
                    scan.nextLine();
                    String one = scan.nextLine();
                    if (one.equals("1")) {
                        manager.clearAll();
                        System.out.println(empty);
                    } else {
                        printMenu();
                    }
                }

            } else if (command == 0) {
                System.out.println("До свидания!");
                break;
            } else {
                System.out.println(noCommand);
            }
        }
    }

    public static void printMenu() {
        System.out.println();
        System.out.println("Программа `Трекер задач` v 1.0");
        System.out.println();
        System.out.println("  =Ниже на ваш выбор приведены операции с задачами=");
        System.out.println();
        System.out.println("1 -    Получить списки задач");
        System.out.println("2 -    Получить задачу по ID");
        System.out.println("3 -    Получить все подзадачи (SubEpic) в задаче (Epic)");
        System.out.println("4 -    Создать задачу");
        System.out.println("5 -    Обновить задачу");
        System.out.println("6 -    Узнать статус по ID");
        System.out.println();
        System.out.println("7 -             Удалить задачу по ID");
        System.out.println("8 -             Удалить все задачи");
        System.out.println();
        System.out.println("0 -             Выйти");
        System.out.println();
    }

    public static void printSubMenu() {
        System.out.println("Выберете задачи:");
        System.out.println("1 - Простые задачи (Simple Task)");
        System.out.println("2 - Задачи с подзадачами (Epic)");
        System.out.println("3 - Подзадачи (SubEpic)");
    }
}