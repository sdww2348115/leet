public class Solution {
    public int strStr(String haystack, String needle) {
    if(needle.length() == 0) {
        return 0;
    }
    //计算needle的左移数组
    int[] array = new int[needle.length()];
    array[0] = 1;
    for(int i = 1; i < needle.length(); i++) {
      for(int startIndex = array[i-1]; startIndex < i; startIndex++) {
        if(strComp(needle, startIndex, i - 1)) {
          array[i] = startIndex;
          break;
        }
      }
      if(array[i] == 0) array[i] = i;
    }
    //具体算法
    int hIndex = 0;
    int nIndex = 0;
    for(;;) {
      if((haystack.length() - hIndex) < (needle.length() - nIndex)) return -1;
      if(nIndex == needle.length()) return hIndex - nIndex;
      if(haystack.charAt(hIndex) == needle.charAt(nIndex)) {
        hIndex++;
        nIndex++;
      }
      else {
        if(nIndex == 0) {
          hIndex++;
        } else {
          nIndex -= array[nIndex];
        }
      }
    }
  }

  private boolean strComp(String str, int startIndex, int endIndex) {
    for(int i = 0; i <= endIndex - startIndex; i++) {
      if(str.charAt(i) != str.charAt(i + startIndex)) {
        return false;
      }
    }
    return true;
  }
}