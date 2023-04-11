import Tasks.Epic;
import Tasks.SimpleTask;
import Tasks.SubEpic;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int nextId = 1;
    public ArrayList<Integer> allIds = new ArrayList<>();
    public ArrayList<Integer> simpleTaskIds = new ArrayList<>();
    public ArrayList<Integer> epicIds = new ArrayList<>();
    public ArrayList<Integer> subEpicIds = new ArrayList<>();

    public HashMap<Integer, SimpleTask> simpleTasks = new HashMap<>();
    public HashMap<Integer, SubEpic> subEpics = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();

    public HashMap<Integer, ArrayList<Integer>> epicsGoSubEpics = new HashMap<>();

    public int createSimpleTask(SimpleTask simpleTask) {
        simpleTask.setId(nextId);
        allIds.add(nextId);
        simpleTaskIds.add(nextId);
        nextId++;
        simpleTasks.put(simpleTask.getId(), simpleTask);
        return simpleTask.getId();
    }

    public void updateSimpleTask(SimpleTask simpleTask) {
        simpleTasks.put(simpleTask.getId(), simpleTask);
    }

    public int createEpic(Epic epic) {
        epic.setId(nextId);
        allIds.add(nextId);
        epicIds.add(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public int createSubEpic(SubEpic subEpic) {
        ArrayList<Integer> tempSubIds = new ArrayList<>();

        subEpic.setId(nextId);
        allIds.add(nextId);
        subEpicIds.add(nextId);
        subEpics.put(subEpic.getId(), subEpic);
        int epId = getSubIdById(nextId);
        Epic epic = epics.get(epId);

        if (!epicsGoSubEpics.containsKey(epId)) {
            epicsGoSubEpics.put(epId, tempSubIds);
        }
        epicsGoSubEpics.get(epId).add(nextId);
        epic.setSubEpicIds(epicsGoSubEpics.get(epId));
        nextId++;
        return subEpic.getId();
    }

    public void updateSubEpic(SubEpic subEpic) {
        subEpics.put(subEpic.getId(), subEpic);

        int epId = subEpic.getEpicId();
        Boolean status;
        int n = 0; //new
        int p = 0;//in progress
        int d = 0;//done
        for (Integer subId : epics.get(epId).getSubEpicIds()) {
            if (subEpics.get(subId).isStatus() == null) {
                n++;
            } else if (subEpics.get(subId).isStatus()) {
                p++;
            } else if (!subEpics.get(subId).isStatus()) {
                d++;
            }
            if ((n > 0) && (p == 0) && (d == 0)) {
                status = null;
                epics.get(epId).setStatus(status);

            } else if ((d > 0) && (n == 0) && (p == 0)) {
                status = false;
                epics.get(epId).setStatus(status);
            } else {
                status = true;
                epics.get(epId).setStatus(status);
            }
        }
    }

    public void removeForId(int id) {
        allIds.remove(Integer.valueOf(id));

        if (simpleTasks.containsKey(id)) {
            simpleTasks.remove(id);
            simpleTaskIds.remove(Integer.valueOf(id));

        } else if (!epicsGoSubEpics.get(id).isEmpty()) {
            for (int i : epicsGoSubEpics.get(id)) {
                subEpics.remove(i);
                subEpicIds.remove(Integer.valueOf(i));
                allIds.remove(Integer.valueOf(i));

            } if (epics.containsKey(id)) {
                epics.remove(id);
                epicIds.remove(Integer.valueOf(id));
                epicsGoSubEpics.remove(id);

            } else {
                subEpics.remove(id);
            }
            System.out.println("Задача № " + id + " удалена!");
        }
    }

    public void clearAll () {
        allIds.clear();

        if (!simpleTasks.isEmpty()) {
            simpleTasks.clear();
            simpleTaskIds.clear();

        } else if (!epics.isEmpty()) {
            epics.clear();
            epicIds.clear();

        } else if (!subEpics.isEmpty()) {
            subEpics.clear();
            subEpicIds.clear();
        }
        nextId = 1;
    }

    public void getStatusByIds ( int id){
        Boolean status = null;
        if (simpleTasks.containsKey(id)) {
            status = simpleTasks.get(id).isStatus();
        } else if (epics.containsKey(id)) {
            status = epics.get(id).isStatus();
        } else if (subEpics.containsKey(id)) {
            status = subEpics.get(id).isStatus();
        }
        System.out.println(status);
    }

    public int getSubIdById ( int id){
        return subEpics.get(id).getEpicId();
    }

    public boolean checkForEmpty () {
        return allIds.isEmpty();
    }

    @Override
    public String toString () {
        return "Manager{" +
                "simpleTasks=" + simpleTasks +
                ", subEpics=" + subEpics +
                ", epics=" + epics +
                '}';
    }
}