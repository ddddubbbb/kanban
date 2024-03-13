import managers.task.FileBackedTasksManager;
import managers.task.InMemoryTaskManager;
import managers.Managers;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import enums.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) throws IOException {

        FileBackedTasksManager fileBackedTasksManager = Managers.getBackedDefault();
        fileBackedTasksManager.loadFromFile();

        Scanner scan = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


        while (true) {
            String noCommand = "Такой команды нет, попробуйте снова.";
            String noId = "с таким ID Отсутствует! Попробуйте снова.";
            String empty = "Список задач пуст.";

            printMenu();
            int command = scan.nextInt();

            if (command == 1) {

                System.out.println("Сортировать задачи по приоритету?");
                System.out.println("1 - ДА  |  2 - НЕТ");
                int command1_2 = scan.nextInt();
                if (command1_2 == 1) {
                    if (fileBackedTasksManager.delegate.isEmpty()) {
                        System.out.println(empty);
                    }
                    System.out.println("Список задач по приоритету стартового времени (кроме Epic):");
                    System.out.println(fileBackedTasksManager.delegate.getTasksStartTimeTree());

                } else if (command1_2 == 2) {
                    System.out.println("Список всех задач:");
                    if (fileBackedTasksManager.delegate.isEmpty()) {
                        System.out.println(empty);
                    }

                    System.out.println(fileBackedTasksManager.delegate.getTasks());
                    System.out.println(fileBackedTasksManager.delegate.getEpics());
                    System.out.println(fileBackedTasksManager.delegate.getSubTasks());

                } else {
                    System.out.println(noCommand);
                }

            } else if (command == 2) {

                if (fileBackedTasksManager.delegate.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех доступных Task ID: " + fileBackedTasksManager.delegate.getTasksMap().keySet() + ", "
                            + "Epic ID: " + fileBackedTasksManager.delegate.getEpicsMap().keySet() + ", "
                            + " SubTask ID: " + fileBackedTasksManager.delegate.getSubTasksMap().keySet() + ".");
                    System.out.println("Введите ID задачи: ");
                    int scanID = scan.nextInt();
                    if (fileBackedTasksManager.delegate.getTasksMap().containsKey(scanID)) {
                        System.out.println(fileBackedTasksManager.getTaskForId(scanID));

                    } else if (fileBackedTasksManager.delegate.getEpicsMap().containsKey(scanID)) {
                        System.out.println(fileBackedTasksManager.getEpicForId(scanID));

                    } else if (fileBackedTasksManager.delegate.getSubTasksMap().containsKey(scanID)) {
                        System.out.println(fileBackedTasksManager.getSubTaskForId(scanID));

                    } else {
                        System.out.println("Задача " + noId);
                    }
                }

            } else if (command == 3) {

                if (fileBackedTasksManager.delegate.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех Epic'ов по ID: " + fileBackedTasksManager.delegate.getEpicsMap().keySet());
                    System.out.println("Введите ID задачи (Epic)");
                    int epicID = scan.nextInt();
                    if (fileBackedTasksManager.delegate.getEpicsMap().containsKey(epicID)) {
                        fileBackedTasksManager.delegate.getSubTaskInEpic(epicID);
                        System.out.println(fileBackedTasksManager.delegate.getEpicsMap().get(epicID).getSubTaskIds());
                        System.out.println("        Для задачи : ");
                        System.out.println(fileBackedTasksManager.delegate.getEpicsMap().get(epicID));
                        System.out.println("    Список подзадач: ");
                        System.out.println("    " + fileBackedTasksManager.delegate.getSubTaskInEpic(epicID));

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

                    System.out.println("Когда приступаем?");
                    System.out.println("Введите дату время: ДД.ММ.ГГГГ ЧЧ:mm: ");

                    String stringStartTime = scan.nextLine();
                    LocalDateTime startTime = LocalDateTime.parse(stringStartTime, formatter);

                    System.out.println("Сколько времени нужно, чтобы выполнить " + title + "?");
                    System.out.println("Введите количество минут:");

                    String durationS = scan.nextLine();
                    long durationL = Long.parseLong(durationS);
                    Duration duration = Duration.ofMinutes(durationL);

                    Task newTask = new Task(title, 0, description, TaskStatus.NEW, startTime, duration);
                    System.out.println("Task с ID '" + fileBackedTasksManager.createTask(newTask).getId() + "' создан!");

                } else if (subCommand == 2) {

                    System.out.println(" Для нового Epic'а ");
                    System.out.println("Введите название: ");
                    scan.nextLine();
                    String title = scan.nextLine();
                    System.out.println("Введите описание: ");
                    String description = scan.nextLine();

                    Epic newEpic = new Epic(title, 0, description, null, null);
                    System.out.println("Epic с ID '" + fileBackedTasksManager.createEpic(newEpic).getId() + "' создан!");

                } else if (subCommand == 3) {

                    if (fileBackedTasksManager.delegate.getEpicsMap().isEmpty()) {
                        System.out.println("Для создания SubTask'а нужен Epic.");
                        System.out.println("Сначала создайте Epic!");
                    } else {
                        System.out.println("К какому Epic'у будет отнесен ваш SubTask?");
                        System.out.println("Введите ID Epic'a: ");
                        System.out.println("Список доступных ID: " + fileBackedTasksManager.delegate.getEpicsMap().keySet());
                        int epicId = scan.nextInt();
                        if (!fileBackedTasksManager.delegate.getEpicsMap().containsKey(epicId)) {
                            System.out.println("Epic " + noId);
                            return;
                        }
                        System.out.println("Введите название SubTask'а: ");
                        scan.nextLine();
                        String title = scan.nextLine();
                        System.out.println("Введите описание: ");
                        String description = scan.nextLine();

                        System.out.println("Когда приступаем?");
                        System.out.println("Введите дату время: ДД.ММ.ГГГГ ЧЧ:mm");

                        String stringStartTime = scan.nextLine();
                        LocalDateTime startTime = LocalDateTime.parse(stringStartTime, formatter);

                        System.out.println("Сколько времени нужно, чтобы выполнить " + title + "?");
                        System.out.println("Введите количество минут:");

                        String durationS = scan.nextLine();
                        long durationL = Long.parseLong(durationS);
                        Duration duration = Duration.ofMinutes(durationL);

                        SubTask newSubTask = new SubTask(title, 0, description, epicId, TaskStatus.NEW, startTime, duration);
                        System.out.println("SubTask с ID ' :" + fileBackedTasksManager.createSubTask(newSubTask).getId() + "' создан!");
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
                    System.out.println("Список доступных ID: " + fileBackedTasksManager.delegate.getTasksMap().keySet());
                    int id = scan.nextInt();
                    if (fileBackedTasksManager.delegate.getTasksMap().containsKey(id)) {
                        System.out.println("Обновите название: ");
                        String title = scan.nextLine();
                        scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        System.out.println("Введите статус в формате: ");
                        System.out.println("для 'НОВОГО' - NEW, для 'В ПРОЦЕССЕ' - IN_PROGRESS, для 'ЗАВЕРШЕНО' - DONE");
                        String statusValue = scan.nextLine();
                        TaskStatus status = TaskStatus.valueOf(statusValue);

                        System.out.println("Обновим дату: ");
                        System.out.println("Введите дату время: ДД.ММ.ГГГГ ЧЧ:mm");

                        String stringStartTime = scan.nextLine();
                        LocalDateTime startTime = LocalDateTime.parse(stringStartTime, formatter);

                        System.out.println("Обновим время выполнения " + title + "?");
                        System.out.println("Введите количество минут:");

                        String durationS = scan.nextLine();
                        long durationL = Long.parseLong(durationS);
                        Duration duration = Duration.ofMinutes(durationL);

                        Task newTask = new Task(title, id, description, status, startTime, duration);
                        fileBackedTasksManager.updateTask(newTask);
                        System.out.println("Task обновлен!");
                    } else {
                        System.out.println("Task " + noId);
                    }

                } else if (subCommand == 2) {

                    System.out.println(" Для обновления Epica'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + fileBackedTasksManager.delegate.getEpicsMap().keySet());
                    int id = scan.nextInt();
                    if (fileBackedTasksManager.delegate.getEpicsMap().containsKey(id)) {
                        System.out.println("Обновите название: ");
                        scan.nextLine();
                        String title = scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();


                        if (!fileBackedTasksManager.delegate.getFileBackedTasksMap().isEmpty()) {


                            LocalDateTime startTime = fileBackedTasksManager.delegate.getFileBackedTasksMap().get(id).getStartTime();
                            Duration duration = fileBackedTasksManager.delegate.getFileBackedTasksMap().get(id).getDuration();

                            Epic newEpic = new Epic(title, id, description, startTime, duration);
                            fileBackedTasksManager.updateEpic(newEpic);
                        } else {

                            Epic newEpic = new Epic(title, id, description, null, null);
                            fileBackedTasksManager.updateEpic(newEpic);
                        }
                        fileBackedTasksManager.delegate.updateEpicStatus(id);
                        System.out.println("Epic обновлен!");
                    } else {
                        System.out.println("Epic " + noId);
                    }

                } else if (subCommand == 3) {

                    System.out.println(" Для обновления SubTaska'а ");
                    System.out.println(" Введите ID ");
                    System.out.println("Список доступных ID: " + fileBackedTasksManager.delegate.getSubTasksMap().keySet());
                    int id = scan.nextInt();
                    if (fileBackedTasksManager.delegate.getSubTasksMap().containsKey(id)) {
                        System.out.println("Обновите название: ");
                        String title = scan.nextLine();
                        scan.nextLine();
                        System.out.println("Обновите описание: ");
                        String description = scan.nextLine();
                        System.out.println("Обновите статус в формате: ");
                        System.out.println("для 'НОВОГО' - NEW, для 'В ПРОЦЕССЕ' - IN_PROGRESS, для 'ЗАВЕРШЕНО' - DONE");
                        String statusValue = scan.nextLine();
                        TaskStatus status = TaskStatus.valueOf(statusValue);
                        int epicId = fileBackedTasksManager.delegate.getSubIdById(id);

                        System.out.println("Обновим дату: ");
                        System.out.println("Введите дату время: ДД.ММ.ГГГГ ЧЧ:mm");

                        String stringStartTime = scan.nextLine();
                        LocalDateTime startTime = LocalDateTime.parse(stringStartTime, formatter);

                        System.out.println("Обновим время выполнения " + title + "?");
                        System.out.println("Введите количество минут:");

                        String durationS = scan.nextLine();
                        long durationL = Long.parseLong(durationS);
                        Duration duration = Duration.ofMinutes(durationL);

                        SubTask newSubTask = new SubTask(title, id, description, epicId, status, startTime, duration);
                        fileBackedTasksManager.updateSubTask(newSubTask);
                        System.out.println("SubTask обновлен!");
                    } else {
                        System.out.println("SubTask " + noId);
                    }
                } else {
                    System.out.println(noCommand);
                }

            } else if (command == 6) {

                String status = null;
                if (fileBackedTasksManager.delegate.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех доступных Task ID: " + fileBackedTasksManager.delegate.getTasksMap().keySet() + ", "
                            + "Epic ID: " + fileBackedTasksManager.delegate.getEpicsMap().keySet() + ", "
                            + " SubTask ID: " + fileBackedTasksManager.delegate.getSubTasksMap().keySet() + ".");
                    System.out.println("Чтобы узнать статус задачи - введите ID: ");
                    int id = scan.nextInt();
                    System.out.println();

                    if (fileBackedTasksManager.delegate.getTasksMap().containsKey(id)) {
                        status = String.valueOf(fileBackedTasksManager.delegate.getTaskStatusById(id));
                    } else if (fileBackedTasksManager.delegate.getEpicsMap().containsKey(id)) {
                        status = String.valueOf(fileBackedTasksManager.delegate.getEpicStatusById(id));
                    } else if (fileBackedTasksManager.delegate.getSubTasksMap().containsKey(id)) {
                        status = String.valueOf(fileBackedTasksManager.delegate.getSubTaskStatusById(id));
                    } else {
                        System.out.println("Задача " + noId);
                    }
                    System.out.println("Статус задачи " + id + " = " + status);
                }

            } else if (command == 7) {

                if (fileBackedTasksManager.delegate.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Cписок последних просмотренных задач по их ID (из пункта меню #2):");
                    System.out.println(fileBackedTasksManager.delegate.getHistory());
                }

            } else if (command == 8) {

                if (fileBackedTasksManager.delegate.isEmpty()) {
                    System.out.println(empty);

                } else {
                    System.out.println("Список всех доступных Task ID: " + fileBackedTasksManager.delegate.getTasksMap().keySet() + ", "
                            + "Epic ID: " + fileBackedTasksManager.delegate.getEpicsMap().keySet() + ", "
                            + " SubTask ID: " + fileBackedTasksManager.delegate.getSubTasksMap().keySet() + ".");
                    System.out.println("Если вы удаляете Epic, в нем бу0" +
                            "дут удалены все SubTask'и!!!");
                    System.out.println("Чтобы удалить задачу - введите её ID: ");
                    int id = scan.nextInt();

                    if (fileBackedTasksManager.delegate.getTasksMap().containsKey(id)) {
                        fileBackedTasksManager.taskRemoveForId(id);
                    } else if (fileBackedTasksManager.delegate.getSubTasksMap().containsKey(id)) {
                        fileBackedTasksManager.subTaskRemoveForId(id);
                    } else if (fileBackedTasksManager.delegate.getEpicsMap().containsKey(id)) {
                        fileBackedTasksManager.epicRemoveForId(id);
                    } else {
                        System.out.println("Задача " + noId);
                    }
                    System.out.println("Задача № " + id + " удалена!");
                }

            } else if (command == 9) {

                printSubMenu();
                int subCommand = scan.nextInt();
                if (fileBackedTasksManager.delegate.isEmpty()) {
                    System.out.println(empty);
                } else {
                    System.out.println("Какую задачу хотите удалить");
                    if (subCommand == 1) {
                        fileBackedTasksManager.deleteTasks();
                        System.out.println("Все Task`и удалены!");
                    } else if (subCommand == 2) {
                        fileBackedTasksManager.deleteEpics();
                        System.out.println("Все Epic`и и их SubTask`и удалены!");
                    } else if (subCommand == 3) {
                        fileBackedTasksManager.deleteSubTasks();
                        System.out.println("Все SubTask`и удалены!");
                    }
                }

            } else if (command == 10) {

                if (fileBackedTasksManager.delegate.isEmpty()) {
                    System.out.println(empty);
                } else {
                    System.out.println("Вы уверены, что хотите удалить ВСЕ задачи?");
                    System.out.println("Введите символ '1', если хотите продолжить");
                    System.out.println("Любой другой символ, чтобы выйти в главное меню");
                    scan.nextLine();
                    String one = scan.nextLine();
                    if (one.equals("1")) {
                        fileBackedTasksManager.clearAll();
                        System.out.println(empty);
                    } else {
                        printMenu();
                    }
                }

            } else if (command == 0) {

                System.out.println("До свидания!");
                fileBackedTasksManager.save();
                break;
            } else {
                System.out.println(noCommand);
            }
        }
    }

    public static void printMenu() {
        System.out.println();
        System.out.println("Программа `Трекер задач` v 7.2");
        System.out.println();
        System.out.println("  =Ниже на ваш выбор приведены операции с задачами=");
        System.out.println();
        System.out.println("1  -    Получить списки всех задач");
        System.out.println("2  -    Получить задачу по ID");
        System.out.println("3  -    Получить все подзадачи (SubTask) в задаче (Epic)");
        System.out.println("4  -    Создать задачу");
        System.out.println("5  -    Обновить задачу");
        System.out.println("6  -    Узнать статус задачи по ID");
        System.out.println("7  -    Узнать список просмотренных задач (из п. #2)");
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