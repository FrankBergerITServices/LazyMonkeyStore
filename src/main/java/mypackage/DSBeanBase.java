package mypackage;

import java.util.HashMap;
import java.util.Map;

/**
 * $Id: DSBeanBase.java 76 2005-11-12 14:30:49Z frank $
 * Base class for all JavaBeans we use
 * 
 * here we define some handy methods (e.g. input validation and error handling)
 * we need in all Beanclasses.
 * 
 */
abstract class DSBeanBase {
  protected int error_counter = 0;
  private Map errorMap = new HashMap();

  /**
   * Returns the error message for the given field key, we only support one
   * error message per field key. The message is return as objecttype Object to
   * avoid an cast to type String.
   * 
   * @param key   field key, for which you want the error message
   * @return      the error message as type Object
   */
  public Object getError(String key) { 
    return errorMap.get(key)==null?"":errorMap.get(key);
  }

  /**
   * Performs user input validation for type String.
   * 
   * @param in        the String you got from the HTTP Request
   * @param key       field key, where to put error messages in
   * @param required  is that an optional or required filed?
   * @return          validated String
   */
  public String processString(String in, String key, boolean required) {
    if(in == null || in.length() == 0) {
      if(required) {
        error_counter++;
        errorMap.put(key, "value required");
      }
      return "";
    }
    return in;
  }

  /**
   * Performs user input validation for type int array
   * 
   * @param in        String array you got from HTTP Request
   * @param key       field key, where to put error messages in
   * @param required  is that an optional or required filed?
   * @return          validated int array
   */
  public int[] processIntArray(String[] in, String key, boolean required) {
    int[] out = {};
    if(in == null) {
      if(required) {
        error_counter++;
        errorMap.put(key, "value required");
      }
      return out;
    }
    
    int in_length = in.length;
    out = new int[in_length];
    for(int i=0; i<in_length; i++) {
      try {
        out[i] = Integer.parseInt(in[i]);
      } catch(Exception e) {
         errorMap.put(key, "invalid numeric value!");
         error_counter++;
      }
    }
    
    return out;
  }

  /**
   * Performs user input validation for type int array
   * 
   * @param in          String array you got from HTTP Request
   * @param key         field key, where to put error messages in
   * @param required    is that an optional or required filed?
   * @param length      minimum length of the returned array
   * @param init_value  default value to set, if we need to create new elements
   * @return            validated int array
   */
  public int[] processIntArray(String[] in, String key, boolean required, int length, int init_value) {
    int in_length = 0;
    
    if(in == null) {
      if(required) {
        error_counter++;
        errorMap.put(key, "value required");
      }
    }
    else {
      in_length = in.length;
      if(in_length > length) length = in_length;
    }
    
    int[] out = new int[length];
    
    for(int i=0; i<in_length; i++) {
      try {
        out[i] = Integer.parseInt(in[i]);
      } catch(Exception e) {
         errorMap.put(key, "invalid numeric value!");
         error_counter++;
      }
    }
    for(int i=in_length; i<length; i++) {
      out[i] = init_value;
    }
    
    return out;
  }

  /**
   * Performs user input validation for type int
   * 
   * @param in        String you got from HTTP Request
   * @param key       field key, where to put error messages in
   * @param required  is that an optional or required filed?
   * @return          validated int
   */
  public int processInt(String in, String key, boolean required) {
    if(in == null || in.length() == 0) {
      if(required) {
        error_counter++;
        errorMap.put(key, "value required");
      }
      return 0;
    }
    
    int out = 0;
    try {
      out = Integer.parseInt(in);
    } catch(Exception e) {
       errorMap.put(key, "invalid numeric value!");
       error_counter++;
    }
    
    return out;
  }
}
