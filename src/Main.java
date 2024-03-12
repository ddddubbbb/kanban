import manager.Manager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Scanner scan = new Scanner(System.in);

        while (true) {
            String noCommand = "Такой команды нет, поробуйте снова.";
            String noId = "с таким ID Отсутствует! Пробуйте снова.";
            String empty = "Список задач пауст.";

            printMenu();
            int command = scan.nextInt();

            if (command == 1) {

                if (manager.isEmpty()) {
                    System.out.println(empty);
                }

                System.out.println(manager.getTasksMap());
                System.out.println(manager.getEpicsMap());
                System.out.println(manager.getSubTasksMap());

            } else if (command == 2) {

                if (manager.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех доступных Task ID: " + manager.getTasksMap().keySet() + ", "
                            + "Epic ID: " + manager.getEpicsMap().keySet() + ", "
                            + " SubTask ID: " + manager.getTasksMap().keySet() + ".");
                    System.out.println("Введите ID задачи: ");
                    int scanID = scan.nextInt();
                    if (manager.getTasksMap().containsKey(scanID)) {
                        System.out.println(manager.getTaskForId(scanID));

                    } else if (manager.getEpicsMap().containsKey(scanID)) {
                        System.out.println(manager.getEpicForId(scanID));

                    } else if (manager.getSubTasksMap().containsKey(scanID)) {
                        System.out.println(manager.getSubTaskForId(scanID));

                    } else {
                        System.out.println("Задача " + noId);
                    }
                }

            } else if (command == 3) {

                if (manager.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех Epic'ов по ID: " + manager.getEpicsMap().keySet());
                    System.out.println("Введите ID задачи (Epic)");
                    int epicID = scan.nextInt();
                    if (manager.getEpicsMap().containsKey(epicID)) {
                        manager.getSubTaskInEpic(epicID);
                        System.out.println(manager.getEpicsMap().get(epicID).getSubTaskIds());
                        System.out.println("        Для задачи : ");
                        System.out.println(manager.getEpicsMap().get(epicID));
                        System.out.println("    Список подзадач: ");
                        System.out.println("    " + manager.getSubTaskInEpic(epicID));

                    } else {
                        System.out.println("Задача " + noId);
                    }
                }

            } else if (command == 4) {

                System.out.println("Какую задачу хотите создать?");
                printSubMenu();

                int subCommand = scan.nextInt();

                if (subCommand == 1) {

                    System.out.println(" Для нового Task'а ");
                    System.out.println("Введите название: ");
                    scan.nextLine();
                    String title = scan.nextLine();
                    System.out.println("Введите описание: ");
                    String description = scan.nextLine();
                    Task newTask = new Task(title, 0, description, "NEW");
                    System.out.println("Task с ID '" + manager.createTask(newTask) + "' создан!");

                } else if (subCommand == 2) {

                    System.out.println(" Для нового Epic'а ");
                    System.out.println("Введите название: ");
                    scan.nextLine();
                    String title = scan.nextLine();
                    System.out.println("Введите описание: ");
                    String description = scan.nextLine();
                    Epic newEpic = new Epic(title, 0, description);
                    System.out.println("Sub Epic с ID '" + manager.createEpic(newEpic) + "' создан!");

                } else if (subCommand == 3) {

                    if (manager.getEpicsMap().isEmpty()) {
                        System.out.println("Для создания SubTask'а нужен Epic.");
                        System.out.println("Сначала создайте Epic!");
                    } else {
                        System.out.println("К какому Epic'у будет отнесен ваш SubTask?");
                        System.out.println("Введите ID Epic'a: ");
                        System.out.println("Список доступных ID: " + manager.getEpicsMap().keySet());
                        int epicId = scan.nextInt();
                        if (!manager.getEpicsMap().containsKey(epicId)) {
                            System.out.println("Epic " + noId);
                            return;
                        }
                        System.out.println("Введите название SubTask'а: ");
                        String title = scan.nextLine();
                        scan.nextLine();
                        System.out.println("Введите описание: ");
                        String description = scan.nextLine();
                        SubTask newSubTask = new SubTask(title, 0, description, epicId, "NEW");
                        System.out.println("SubTask с ID '" + manager.createSubTask(newSubTask) + "' создан!");
                    }
                } else {
                    System.out.println(noCommand);
                }

            } else if (command == 5) {

                System.out.println("Какую задачу хотите обновить?");
                printSubMenu();
                int subCommand = scan.nextInt();

                if (subCommand == 1) {

                    System.out.println(" Для обновления Task'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + manager.getTasksMap().keySet());
                    int id = scan.nextInt();
                    if (manager.getTasksMap().containsKey(id)) {
                        System.out.println("Обновите название: ");
                        String title = scan.nextLine();
                        scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        System.out.println("Введите статус в формате: ");
                        System.out.println("для 'НОВОГО' - NEW, для 'В ПРОЦЕССЕ' - IN_PROCESS, для 'ЗАВЕРШЕНО' - DONE");
                        String status = scan.nextLine();
                        Task newTask = new Task(title, id, description, status);
                        manager.updateTask(newTask);
                        System.out.println("Task обновлен!");
                    } else {
                        System.out.println("Task " + noId);
                    }

                } else if (subCommand == 2) {

                    System.out.println(" Для обновления Epica'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + manager.getEpicsMap().keySet());
                    int id = scan.nextInt();
                    if (manager.getEpicsMap().containsKey(id)) {
                        System.out.println("Обновите название: ");
                        String title = scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        Epic newEpic = new Epic(title, id, description);
                        manager.updateEpic(newEpic);
                        manager.updateEpicStatus(id);
                        System.out.println("Epic обновлен!");
                    } else {
                        System.out.println("Epic " + noId);
                    }

                } else if (subCommand == 3) {

                    System.out.println(" Для обновления SubTaska'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + manager.getSubTasksMap().keySet());
                    int id = scan.nextInt();
                    if (manager.getSubTasksMap().containsKey(id)) {
                        System.out.println("Обновите название: ");
                        String title = scan.nextLine();
                        scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        System.out.println("Обновите статус в формате: ");
                        System.out.println("для 'НОВОГО' - NEW, для 'В ПРОЦЕССЕ' - IN_PROCESS, для 'ЗАВЕРШЕНО' - DONE");
                        String status = scan.nextLine();
                        int epicId = manager.getSubIdById(id);
                        SubTask newSubTask = new SubTask(title, id, description, epicId, status);
                        manager.updateSubTask(newSubTask);
                        System.out.println("SubTask обновлен!");
                    } else {
                        System.out.println("SubTask " + noId);
                    }
                } else {
                    System.out.println(noCommand);
                }

            } else if (command == 6) {

                String status = null;
                if (manager.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех доступных Task ID: " + manager.getTasksMap().keySet() + ", "
                            + "Epic ID: " + manager.getEpicsMap().keySet() + ", "
                            + " SubTask ID: " + manager.getTasksMap().keySet() + ".");
                    System.out.println("Чтобы узнать статус задачи - введите ID: ");
                    int id = scan.nextInt();
                    System.out.println();

                    if (manager.getTasksMap().containsKey(id)) {
                        status = manager.getTaskStatusById(id);
                    } else if (manager.getEpicsMap().containsKey(id)) {
                        status = manager.getEpicStatusById(id);
                    } else if (manager.getSubTasksMap().containsKey(id)) {
                        status = manager.getSubTaskStatusById(id);
                    } else {
                        System.out.println("Задача " + noId);
                    }
                    System.out.println("Статус задачи " + id + " = " + status);
                }

            } else if (command == 7) {

                if (manager.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех доступных Task ID: " + manager.getTasksMap().keySet() + ", "
                            + "Epic ID: " + manager.getEpicsMap().keySet() + ", "
                            + " SubTask ID: " + manager.getSubTasksMap().keySet() + ".");
                    System.out.println("Если вы удаляете Epic, в нем будут удалены все SubTask'и!!!");
                    System.out.println("Чтобы удалить задачу - введите её ID: ");
                    int id = scan.nextInt();

                    if (manager.getTasksMap().containsKey(id)) {
                        manager.taskRemoveForId(id);
                    } else if (manager.getSubTasksMap().containsKey(id)) {
                        manager.subTaskRemoveForId(id);
                    } else if (manager.getEpicsMap().containsKey(id)) {
                        manager.epicRemoveForId(id);
                    } else {
                        System.out.println("Задача " + noId);
                    }
                    System.out.println("Задача № " + id + " удалена!");
                }

            } else if (command == 8) {
                if (manager.isEmpty()) {
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
        System.out.println("Программа `Трекер задач` v 1.4");
        System.out.println();
        System.out.println("  =Ниже на ваш выбор приведены операции с задачами=");
        System.out.println();
        System.out.println("1 -    Получить списки задач");
        System.out.println("2 -    Получить задачу по ID");
        System.out.println("3 -    Получить все подзадачи (SubTask) в задаче (Epic)");
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
        System.out.println("1 - Простые задачи (Task)");
        System.out.println("2 - Задачи с подзадачами (Epic)");
        System.out.println("3 - Подзадачи (SubTask)");
    }
}