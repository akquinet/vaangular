/* 
   Licensed under the Apache License, Version 2.0 (the "License"); you may not
   use this file except in compliance with the License. You may obtain a copy of
   the License at
  
     http://www.apache.org/licenses/LICENSE-2.0
  
   Unless required by applicable law or agreed to in writing, software 
   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
   License for the specific language governing permissions and limitations under
   the License. 
 */
package de.akquinet.engineering.vaadin.vaangular.angular;

import com.vaadin.ui.JavaScriptFunction;

import elemental.json.JsonArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Axel Meier, akquinet engineering GmbH
 */
public class MethodInvoker implements JavaScriptFunction
{
    private static final long serialVersionUID = 1L;

    private final Object object;
    private final Method method;

    public MethodInvoker(final Object object, final Method method)
    {
        this.object = object;
        this.method = method;
    }

    @Override
    public void call(final JsonArray arguments)
    {
        try
        {
            final Class<?>[] paramTypes = method.getParameterTypes();
            final List<Object> paramList = new ArrayList<>(paramTypes.length);
            for (int i = 0; i<paramTypes.length; ++i)
            {
                if (Boolean.class.isAssignableFrom(paramTypes[i]))
                {
                    paramList.add(arguments.getBoolean(i));
                }
                else if (Double.class.isAssignableFrom(paramTypes[i]))
                {
                    paramList.add(arguments.getNumber(i));
                }
                else if (String.class.isAssignableFrom(paramTypes[i]))
                {
                    paramList.add(arguments.getString(i));
                }
                else
                {
                    paramList.add(arguments.get(i));
                }
            }
            method.setAccessible(true);
            method.invoke(object, paramList.toArray());
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }
    
}
