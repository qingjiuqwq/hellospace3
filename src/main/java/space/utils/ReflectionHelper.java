/*
 * 涅槃科技 and 风横
 * https://npyyds.top/
 * https://gitee.com/newNP/
 * https://github.com/NirvanaTec/
 * 最终解释权归涅槃科技所有，涅槃科技版权所有。
 */
package space.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class ReflectionHelper
{

   public static Field findField(Class<?> clazz, String... fieldNames) {
      Exception failed = null;
      for (Class<?> currentClass = clazz; currentClass != null; currentClass = currentClass.getSuperclass()) {
         for (String fieldName : fieldNames) {
            if (!fieldName.isEmpty()) {
               try {
                  Field f = currentClass.getDeclaredField(fieldName);
                  f.setAccessible(true);
                  putInt(f, Unsafe.ARRAY_BOOLEAN_BASE_OFFSET, f.getModifiers() & -17);
                  return f;
               } catch (Exception var9) {
                  failed = var9;
               }
            }
         }
      }
      if (failed != null) {
         failed.fillInStackTrace();
      }
      return null;
   }

   public static void putInt(Object object, long offset, int value) {
      try {
         Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
         Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
         theUnsafeField.setAccessible(true);
         Unsafe unsafe = (Unsafe) theUnsafeField.get(null);
         unsafe.putInt(object, offset, value);
      } catch (Exception e) {
         e.fillInStackTrace();
      }
   }

   public static Object getPrivateValue(final Class<?> classToAccess, final Object instance, final String... fieldNames) {
      Field field = findField(classToAccess, fieldNames);
      if (field != null){
         try {
            return field.get(instance);
         } catch (Exception e) {
            e.fillInStackTrace();
         }
      }
      return null;
   }

   public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final E value, final String... fieldNames) {
      Field field = findField(classToAccess, fieldNames);
      if (field != null){
         try {
            field.set(instance, value);
         } catch (Exception e) {
            e.fillInStackTrace();
         }
      }
   }
}
