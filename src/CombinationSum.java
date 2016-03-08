import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sdww on 16-3-8.
 */
public class CombinationSum {
  class Result {
    public List<Integer> list;
    public int endIndex = 0;
    public int sum = 0;
    public Result(Result result) {
      this.list = new LinkedList<Integer>(result.list);
      this.endIndex = result.endIndex;
      this.sum = result.sum;
    }
    public Result() {
      this.list = new LinkedList<Integer>();
    }
  }

  public List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> total = new LinkedList<>();
    List<Result> resultList = new LinkedList<>();
    Result fResult = new Result();
    fResult.list = new LinkedList<Integer>();
    fResult.endIndex = candidates.length - 1;
    fResult.sum = 0;
    resultList.add(fResult);
    Arrays.sort(candidates);
    while(resultList.size() > 0) {
      Result result = resultList.remove(0);
      for(int i = 0; i <= result.endIndex; i++) {
        int ssum = result.sum + candidates[i];
        if(ssum == target) {
          List<Integer> list = new LinkedList<Integer>(result.list);
          list.add(0, candidates[i]);
          total.add(list);
        } else if(ssum < target) {
          Result newResult = new Result(result);
          newResult.list.add(0, candidates[i]);
          newResult.sum = ssum;
          newResult.endIndex = i;
          resultList.add(newResult);
        }
      }
    }
    return total;
  }

  public static void main(String[] args) {
    int[] a= {8,4,7,3};
    CombinationSum combinationSum = new CombinationSum();
    combinationSum.combinationSum(a, 11);
  }
}
