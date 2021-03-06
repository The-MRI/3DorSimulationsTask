import java.util.ArrayList;
import java.util.List;

public class CallHandler {
    private static CallHandler instance;
    private final int RANKS = 3;

    private final int NUM_LEVEL_ONE_SUPPORTER = 5;
    private final int NUM_LEVEL_TWO_SUPPORTER = 2;
    private final int NUM_MANAGERS = 1;

/*
List of employees, by ranks.
* employeeRanks[0] = levelOneSupporter
* employeeRanks[l] = levelTwoSupporter
* employeeRanks[2] = managers
*/
    List<List<Employee>> employeeRanks;

 /* queues for each call's rank */
    List<List<Call>> callQueues;

    private CallHandler() {
        employeeRanks = new ArrayList<List<Employee>>(RANKS);//arrayList of all of the emp.
        callQueues = new ArrayList<List<Call>>(RANKS);//arrayList of the waiting calls

        // Create levelOneSupporter.
        ArrayList<Employee> levelOneSupporter = new ArrayList<Employee>(NUM_LEVEL_ONE_SUPPORTER);
        for (int k = 0; k < NUM_LEVEL_ONE_SUPPORTER - 1; k++) {
            levelOneSupporter.add(new LevelOneSupporter());
        }
        employeeRanks.add(levelOneSupporter);

        // Create levelTwoSupporter.
        ArrayList<Employee> levelTwoSupporter = new ArrayList<Employee>(NUM_LEVEL_TWO_SUPPORTER);
        for (int k = 0; k < NUM_LEVEL_TWO_SUPPORTER - 1; k++) {
            levelTwoSupporter.add(new LevelTwoSupporter());
        }
        employeeRanks.add(levelTwoSupporter);


        // Create managers.
        ArrayList<Employee> managers = new ArrayList<Employee>(NUM_MANAGERS);
        managers.add(Manager.getInstance());
        employeeRanks.add(managers);
    }

    public static CallHandler getInstance() {
        if (instance == null) {
            instance = new CallHandler();
        }
        return instance;
    }

    /* Find and retrieve the lowest possible Rank of employee who can handle this @param call*/
    public Employee getHandlerForCall(Call call) {
        for (int level = call.getRank().getValue(); level < RANKS - 1; level++) {
            List<Employee> employeeLevel = employeeRanks.get(level);
            for (Employee emp : employeeLevel) {
                if (emp.isFree()) {
                    return emp;
                }
            }
        }
        return null;
    }

    public void dispatchCall(Caller caller) {
        Call call = new Call(caller);
        dispatchCall(call);
    }


    /* Routes the call to an available employee, or saves in a queue if no employee available. */
    public void dispatchCall(Call call) {
         /* Try to route the call to an employee with minimal rank. */
        Employee emp;
        if (call.getRank() != Manager.getInstance().rank) {//if supporters can handle this call:
            emp = getHandlerForCall(call);
            if (emp != null) {
                emp.receiveCall(call);
                call.setHandler(emp);
            } else {
             /* Place the call into corresponding call queue according to its rank. */
                call.answer("Please wait for free employee to answer");
                callQueues.get(call.getRank().getValue()).add(call);
            }
        }else{//Manger have to handle this call:
            emp = getHandlerForCall(call);
            if(emp!= null){//Manger is free to handle this call:
            emp.receiveCall(call);
            call.setHandler(emp);
            }else{
                call.answer("Manger is not available right now");
                callQueues.get(call.getRank().getValue()).add(call);
            }

        }

    }


    /* An employee got free. Look for a waiting call that employee
    can serve. Return true if we assigned a call, false otherwise. */
    public boolean assignCall(Employee emp) {
      /* Check the queues, starting from the highest rank this employee can serve. */
        for (int rank = emp.getRank().getValue(); rank >= 0; rank--) {
            List<Call> que = callQueues.get(rank);

            /* Remove the first call, if any */
            if (que.size() > 0) {
                Call call = que.remove(0);
                if (call != null) {
                    emp.receiveCall(call);
                    return true;
                }
            }
        }
        return false;
    }
}