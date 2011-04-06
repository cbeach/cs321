// operator error
class testd18 {
  public static void main(String[] a) {
    x = 1+2-3*4/5==6&&7!=8||!9; // OK
    y = 1+2-3*4/5.6&&7!=8||9;   // OK
    (a+b)[2.5] = x;             // OK with the current grammar!
    (a).b(c);                   // OK
    (a).b.(c);                  // error
  }
}
