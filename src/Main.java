import manager.*;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        HistoryManager inMemoryHistoryManager = Managers.getHistoryDefault();
        Scanner scan = new Scanner(System.in);

        while (true) {
            String noCommand = "Такой команды нет, поробуйте снова.";
            String noId = "с таким ID Отсутствует! Пробуйте снова.";
            String empty = "Список задач пауст.";

            printMenu();
            int command = scan.nextInt();

            if (command == 1) {

                if (inMemoryTaskManager.isEmpty()) {
                    System.out.println(empty);
                }

                System.out.println(inMemoryTaskManager.getTasks());
                System.out.println(inMemoryTaskManager.getEpics());
                System.out.println(inMemoryTaskManager.getSubTasks());

            } else if (command == 2) {

                if (inMemoryTaskManager.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех доступных Task ID: " + inMemoryTaskManager.getTasksMap().keySet() + ", "
                            + "Epic ID: " + inMemoryTaskManager.getEpicsMap().keySet() + ", "
                            + " SubTask ID: " + inMemoryTaskManager.getSubTasksMap().keySet() + ".");
                    System.out.println("Введите ID задачи: ");
                    int scanID = scan.nextInt();
                    if (inMemoryTaskManager.getTasksMap().containsKey(scanID)) {
                        System.out.println(inMemoryTaskManager.getTaskForId(scanID));

                    } else if (inMemoryTaskManager.getEpicsMap().containsKey(scanID)) {
                        System.out.println(inMemoryTaskManager.getEpicForId(scanID));

                    } else if (inMemoryTaskManager.getSubTasksMap().containsKey(scanID)) {
                        System.out.println(inMemoryTaskManager.getSubTaskForId(scanID));

                    } else {
                        System.out.println("Задача " + noId);
                    }
                }

            } else if (command == 3) {

                if (inMemoryTaskManager.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех Epic'ов по ID: " + inMemoryTaskManager.getEpicsMap().keySet());
                    System.out.println("Введите ID задачи (Epic)");
                    int epicID = scan.nextInt();
                    if (inMemoryTaskManager.getEpicsMap().containsKey(epicID)) {
                        inMemoryTaskManager.getSubTaskInEpic(epicID);
                        System.out.println(inMemoryTaskManager.getEpicsMap().get(epicID).getSubTaskIds());
                        System.out.println("        Для задачи : ");
                        System.out.println(inMemoryTaskManager.getEpicsMap().get(epicID));
                        System.out.println("    Список подзадач: ");
                        System.out.println("    " + inMemoryTaskManager.getSubTaskInEpic(epicID));

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
                    Task newTask = new Task(title, 0, description, TaskStatus.NEW);
                    System.out.println("Task с ID '" + inMemoryTaskManager.createTask(newTask) + "' создан!");

                } else if (subCommand == 2) {

                    System.out.println(" Для нового Epic'а ");
                    System.out.println("Введите название: ");
                    scan.nextLine();
                    String title = scan.nextLine();
                    System.out.println("Введите описание: ");
                    String description = scan.nextLine();
                    Epic newEpic = new Epic(title, 0, description);
                    System.out.println("Sub Epic с ID '" + inMemoryTaskManager.createEpic(newEpic) + "' создан!");

                } else if (subCommand == 3) {

                    if (inMemoryTaskManager.getEpicsMap().isEmpty()) {
                        System.out.println("Для создания SubTask'а нужен Epic.");
                        System.out.println("Сначала создайте Epic!");
                    } else {
                        System.out.println("К какому Epic'у будет отнесен ваш SubTask?");
                        System.out.println("Введите ID Epic'a: ");
                        System.out.println("Список доступных ID: " + inMemoryTaskManager.getEpicsMap().keySet());
                        int epicId = scan.nextInt();
                        if (!inMemoryTaskManager.getEpicsMap().containsKey(epicId)) {
                            System.out.println("Epic " + noId);
                            return;
                        }
                        System.out.println("Введите название SubTask'а: ");
                        String title = scan.nextLine();
                        scan.nextLine();
                        System.out.println("Введите описание: ");
                        String description = scan.nextLine();
                        SubTask newSubTask = new SubTask(title, 0, description, epicId, TaskStatus.NEW);
                        System.out.println("SubTask с ID '" + inMemoryTaskManager.createSubTask(newSubTask) + "' создан!");
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
                    System.out.println("Список доступных ID: " + inMemoryTaskManager.getTasksMap().keySet());
                    int id = scan.nextInt();
                    if (inMemoryTaskManager.getTasksMap().containsKey(id)) {
                        System.out.println("Обновите название: ");
                        String title = scan.nextLine();
                        scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        System.out.println("Введите статус в формате: ");
                        System.out.println("для 'НОВОГО' - NEW, для 'В ПРОЦЕССЕ' - IN_PROCESS, для 'ЗАВЕРШЕНО' - DONE");
                        String statusValue = scan.nextLine();
                        TaskStatus status = TaskStatus.valueOf(statusValue);
                        Task newTask = new Task(title, id, description, status);
                        inMemoryTaskManager.updateTask(newTask);
                        System.out.println("Task обновлен!");
                    } else {
                        System.out.println("Task " + noId);
                    }

                } else if (subCommand == 2) {

                    System.out.println(" Для обновления Epica'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + inMemoryTaskManager.getEpicsMap().keySet());
                    int id = scan.nextInt();
                    if (inMemoryTaskManager.getEpicsMap().containsKey(id)) {
                        System.out.println("Обновите название: ");
                        String title = scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        Epic newEpic = new Epic(title, id, description);
                        inMemoryTaskManager.updateEpic(newEpic);
                        inMemoryTaskManager.updateEpicStatus(id);
                        System.out.println("Epic обновлен!");
                    } else {
                        System.out.println("Epic " + noId);
                    }

                } else if (subCommand == 3) {

                    System.out.println(" Для обновления SubTaska'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + inMemoryTaskManager.getSubTasksMap().keySet());
                    int id = scan.nextInt();
                    if (inMemoryTaskManager.getSubTasksMap().containsKey(id)) {
                        System.out.println("Обновите название: ");
                        String title = scan.nextLine();
                        scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        System.out.println("Обновите статус в формате: ");
                        System.out.println("для 'НОВОГО' - NEW, для 'В ПРОЦЕССЕ' - IN_PROCESS, для 'ЗАВЕРШЕНО' - DONE");
                        String statusValue = scan.nextLine();
                        TaskStatus status = TaskStatus.valueOf(statusValue);
                        int epicId = inMemoryTaskManager.getSubIdById(id);
                        SubTask newSubTask = new SubTask(title, id, description, epicId, status);
                        inMemoryTaskManager.updateSubTask(newSubTask);
                        System.out.println("SubTask обновлен!");
                    } else {
                        System.out.println("SubTask " + noId);
                    }
                } else {
                    System.out.println(noCommand);
                }

            } else if (command == 6) {

                String status = null;
                if (inMemoryTaskManager.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех доступных Task ID: " + inMemoryTaskManager.getTasksMap().keySet() + ", "
                            + "Epic ID: " + inMemoryTaskManager.getEpicsMap().keySet() + ", "
                            + " SubTask ID: " + inMemoryTaskManager.getTasksMap().keySet() + ".");
                    System.out.println("Чтобы узнать статус задачи - введите ID: ");
                    int id = scan.nextInt();
                    System.out.println();

                    if (inMemoryTaskManager.getTasksMap().containsKey(id)) {
                        status = String.valueOf(inMemoryTaskManager.getTaskStatusById(id));
                    } else if (inMemoryTaskManager.getEpicsMap().containsKey(id)) {
                        status = String.valueOf(inMemoryTaskManager.getEpicStatusById(id));
                    } else if (inMemoryTaskManager.getSubTasksMap().containsKey(id)) {
                        status = String.valueOf(inMemoryTaskManager.getSubTaskStatusById(id));
                    } else {
                        System.out.println("Задача " + noId);
                    }
                    System.out.println("Статус задачи " + id + " = " + status);
                }

            } else if (command == 7) {

                if (inMemoryTaskManager.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Cписок последних 10 просмотренных задач по их идентификатору (из пункта меню #2):");
                    System.out.println(inMemoryHistoryManager.getHistory());
                }

            } else if (command == 8) {

                if (inMemoryTaskManager.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех доступных Task ID: " + inMemoryTaskManager.getTasksMap().keySet() + ", "
                            + "Epic ID: " + inMemoryTaskManager.getEpicsMap().keySet() + ", "
                            + " SubTask ID: " + inMemoryTaskManager.getSubTasksMap().keySet() + ".");
                    System.out.println("Если вы удаляете Epic, в нем будут удалены все SubTask'и!!!");
                    System.out.println("Чтобы удалить задачу - введите её ID: ");
                    int id = scan.nextInt();

                    if (inMemoryTaskManager.getTasksMap().containsKey(id)) {
                        inMemoryTaskManager.taskRemoveForId(id);
                    } else if (inMemoryTaskManager.getSubTasksMap().containsKey(id)) {
                        inMemoryTaskManager.subTaskRemoveForId(id);
                    } else if (inMemoryTaskManager.getEpicsMap().containsKey(id)) {
                        inMemoryTaskManager.epicRemoveForId(id);
                    } else {
                        System.out.println("Задача " + noId);
                    }
                    System.out.println("Задача № " + id + " удалена!");
                }

            } else if (command == 9) {

                printSubMenu();
                int subCommand = scan.nextInt();
                if (inMemoryTaskManager.isEmpty()) {
                    System.out.println(empty);
                } else {
                    System.out.println("Какую задачу хотите удалить");
                    if (subCommand == 1) {
                        inMemoryTaskManager.deleteTasks();
                        System.out.println("Все Task`и удалены!");
                    } else if (subCommand == 2) {
                        inMemoryTaskManager.deleteEpics();
                        System.out.println("Все Epic`и и их SubTask`и удалены!");
                    } else if (subCommand == 3) {
                        inMemoryTaskManager.deleteSubTasks();
                        System.out.println("Все SubTask`и удалены!");
                    }
                }

            } else if (command == 10) {

                if (inMemoryTaskManager.isEmpty()) {
                    System.out.println(empty);
                } else {
                    System.out.println("Вы уверены, что хотите удалить ВСЕ задачи?");
                    System.out.println("Введите символ '1', если хотите продолжить");
                    System.out.println("Любой другой символ, чтобы выйти в главное меню");
                    scan.nextLine();
                    String one = scan.nextLine();
                    if (one.equals("1")) {
                        inMemoryTaskManager.clearAll();
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
        System.out.println("Программа `Трекер задач` v 2.3");
        System.out.println();
        System.out.println("  =Ниже на ваш выбор приведены операции с задачами=");
        System.out.println();
        System.out.println("1  -    Получить списки всех задач");
        System.out.println("2  -    Получить задачу по ID");
        System.out.println("3  -    Получить все подзадачи (SubTask) в задаче (Epic)");
        System.out.println("4  -    Создать задачу");
        System.out.println("5  -    Обновить задачу");
        System.out.println("6  -    Узнать статус задачи по ID");
        System.out.println("7  -    Узнать список последних 10 просмотров задач (из п. #2)");
        System.out.println();
        System.out.println("8  -             Удалить задачу по ID");
        System.out.println("9  -             Удалить задачу по типу(Epic/SubTask/Task)");
        System.out.println("10 -             Удалить все задачи");
        System.out.println();
        System.out.println("0  -             Выйти");
        System.out.println();
    }

    public static void printSubMenu() {
        System.out.println("Выберете задачи:");
        System.out.println("1 - Простые задачи (Task)");
        System.out.println("2 - Задачи с подзадачами (Epic)");
        System.out.println("3 - Подзадачи (SubTask)");
    }
}