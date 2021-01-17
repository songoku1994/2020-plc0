package miniplc0java.syn;

import java.util.ArrayList;

public class ReturnCheck {

    ArrayList<ReturnTuple> returnTuples = new ArrayList<>();

    public ReturnTuple top() {
        return returnTuples.get(returnTuples.size() - 1);
    }

    public ReturnTuple preTop() {
        return returnTuples.get(returnTuples.size() - 2);
    }

    public void getResult() {
        while (true) {
            if (top().returnCls == ReturnCls.FUNCTION) {
                break;
            } else if (top().returnCls == ReturnCls.IF) {
                returnTuples.remove(returnTuples.size() - 1);
                break;
            } else if (top().returnCls == ReturnCls.ELSE
                && preTop().returnCls == ReturnCls.ELSE_IF) {
                if (!top().ifReturn || !preTop().ifReturn) {
                    top().ifReturn = false;
                }
                returnTuples.remove(returnTuples.size() - 2);
            } else if (top().returnCls == ReturnCls.ELSE
                && preTop().returnCls == ReturnCls.IF) {
//                if(returnPoints.get(returnPoints.size()-3).returnEnum!=ReturnEnum.FUNCTION)
//                    throw new Error("123");
                if (top().ifReturn && preTop().ifReturn) {
                    returnTuples.get(returnTuples.size() - 3).ifReturn = true;
                }
                returnTuples.remove(returnTuples.size() - 1);
                returnTuples.remove(returnTuples.size() - 1);
                break;
            } else if (top().returnCls == ReturnCls.ELSE_IF
                && preTop().returnCls == ReturnCls.IF) {
//                if(returnPoints.get(returnPoints.size()-3).returnEnum!=ReturnEnum.FUNCTION)
//                    throw new Error("123");
                returnTuples.remove(returnTuples.size() - 1);
                returnTuples.remove(returnTuples.size() - 1);
                break;
            } else if (top().returnCls == ReturnCls.ELSE_IF
                && preTop().returnCls == ReturnCls.ELSE_IF) {
                returnTuples.remove(returnTuples.size() - 1);
                returnTuples.remove(returnTuples.size() - 1);
            } else if (top().returnCls == ReturnCls.IF
                && preTop().returnCls == ReturnCls.FUNCTION) {
                returnTuples.remove(returnTuples.size() - 1);
            }
        }
    }
}
