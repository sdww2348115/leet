/**
 * Created by sdww on 16-3-8.
 * Implement strStr().
 *
 * Returns the index of the first occurrence of needle in haystack, or -1 if needle is not part of haystack.
 *
 * Subscribe to see which companies asked this question
 *
 */
//KMP算法
public class ImplementStrStr {
  public int strStr(String haystack, String needle) {
    //计算needle的左移数组
    int[] array = new int[needle.length()];
    array[0] = 0;
    for(int i = 1; i < needle.length(); i++) {
      for(int startIndex = 1; startIndex <= i; startIndex++) {
        if(strComp(needle, needle.substring(startIndex, i - 1))) {
          array[i] = startIndex;
        }
      }
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
        if(array[nIndex] == 0) {
          hIndex++;
        } else {
          nIndex -= array[nIndex];
        }
      }
    }
  }

  private boolean strComp(String str1, String str2) {
    if(str1.length() < str2.length()) return false;
    for(int i = 0; i < str2.length(); i++) {
      if(str1.charAt(i) != str2.charAt(i)) {
        return false;
      }
    }
    return true;
  }
}
