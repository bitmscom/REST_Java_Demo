package com.bitms.api.client.bean.sign;

import com.bitms.api.client.constant.BitmsConstants;
import com.bitms.api.client.exception.ApiException;
import com.bitms.api.client.mapping.ApiConverters;
import com.bitms.api.client.mapping.Converter;
import com.bitms.api.client.tool.Encrypt;
import com.bitms.api.client.tool.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON format converter。
 * 
 * @since 1.0, Apr 11, 2010
 */
public class ApiJsonConverter implements ApiConverter
{
    public <T extends ApiResponse> T toResponse(String rsp, Class<T> clazz) throws ApiException
    {
        JSONReader reader = new JSONValidatingReader(new ExceptionErrorListener());
        Object rootObj = reader.read(rsp);
        if (rootObj instanceof Map<?, ?>)
        {
            Map<?, ?> rootJson = (Map<?, ?>) rootObj;
            return fromJson(rootJson, clazz);
        }
        return null;
    }

    /**
      * Convert data in JSON format to objects.
      *
      * @param <T> generic domain object
      * @param json data in JSON format
      * @param clazz generic field type
      * @return domain object
      * @throws ApiException
      */
    public <T> T fromJson(final Map<?, ?> json, Class<T> clazz) throws ApiException
    {
        return ApiConverters.convert(clazz, new Reader()
        {
            public boolean hasReturnField(Object name)
            {
                return json.containsKey(name);
            }
            
            public Object getPrimitiveObject(Object name)
            {
                return json.get(name);
            }
            
            public Object getObject(Object name, Class<?> type) throws ApiException
            {
                Object tmp = json.get(name);
                if (tmp instanceof Map<?, ?>)
                {
                    Map<?, ?> map = (Map<?, ?>) tmp;
                    return fromJson(map, type);
                }
                else
                {
                    return null;
                }
            }
            
            public List<?> getListObjects(Object listName, Object itemName, Class<?> subType) throws ApiException
            {
                List<Object> listObjs = null;
                Object listTmp = json.get(listName);
                if (listTmp instanceof Map<?, ?>)
                {
                    Map<?, ?> jsonMap = (Map<?, ?>) listTmp;
                    Object itemTmp = jsonMap.get(itemName);
                    if (itemTmp == null && listName != null)
                    {
                        String listNameStr = listName.toString();
                        itemTmp = jsonMap.get(listNameStr.substring(0, listNameStr.length() - 1));
                    }
                    if (itemTmp instanceof List<?>)
                    {
                        listObjs = getListObjectsInner(subType, itemTmp);
                    }
                }
                else if (listTmp instanceof List<?>)
                {
                    listObjs = getListObjectsInner(subType, listTmp);
                }
                return listObjs;
            }
            
            private List<Object> getListObjectsInner(Class<?> subType, Object itemTmp) throws ApiException
            {
                List<Object> listObjs;
                listObjs = new ArrayList<Object>();
                List<?> tmpList = (List<?>) itemTmp;
                for (Object subTmp : tmpList)
                {
                    Object obj = null;
                    if (String.class.isAssignableFrom(subType))
                    {
                        obj = subTmp;
                    }
                    else if (Long.class.isAssignableFrom(subType))
                    {
                        obj = subTmp;
                    }
                    else if (Integer.class.isAssignableFrom(subType))
                    {
                        obj = subTmp;
                    }
                    else if (Boolean.class.isAssignableFrom(subType))
                    {
                        obj = subTmp;
                    }
                    else if (subTmp instanceof Map<?, ?>)
                    {// object
                        Map<?, ?> subMap = (Map<?, ?>) subTmp;
                        obj = fromJson(subMap, subType);
                    }
                    if (obj != null)
                    {
                        listObjs.add(obj);
                    }
                }
                return listObjs;
            }
        });
    }
    
    /**
     * @see Converter#getSignItem(BasicRequest, String)
     */
    public SignItem getSignItem(ApiBasicRequest<?> request, String responseBody) throws ApiException
    {
        // Return directly if the response is empty
        if (StringUtils.isEmpty(responseBody)) { return null; }
        SignItem signItem = new SignItem();
        // Get signature
        String sign = getSign(responseBody);
        signItem.setSign(sign);
        // Signature source string
        String signSourceData = getSignSourceData(request, responseBody);
        signItem.setSignSourceDate(signSourceData);
        return signItem;
    }
    
    /**
     *
     * @param request
     * @param body
     * @return
     */
    private String getSignSourceData(ApiBasicRequest<?> request, String body)
    {
        // Add source string starting point
        String rootNode = BitmsConstants.RESPONSE_KEY;
        int indexOfRootNode = body.indexOf(rootNode);
        // Successful or new interface
        if (indexOfRootNode > 0)
        {
            return parseSignSourceData(body, rootNode, indexOfRootNode);
            // Old version failed interface
        }
        else
        {
            return null;
        }
    }
    
    /**
     *   Get the signature source string content
     *
     *
     * @param body
     * @param rootNode
     * @param indexOfRootNode
     * @return
     */
    private String parseSignSourceData(String body, String rootNode, int indexOfRootNode)
    {
        // First letter + length + quotation marks and semicolons
        int signDataStartIndex = indexOfRootNode + rootNode.length() + 2;
        int indexOfSign = body.indexOf("\"" + BitmsConstants.SIGN + "\"");
        if (indexOfSign < 0) { return null; }
        // Pre-signature - comma
        int signDataEndIndex = indexOfSign - 1;
        return body.substring(signDataStartIndex, signDataEndIndex);
    }
    
    /**
     * Get signature
     *
     * @param body
     * @return
     */
    private String getSign(String body)
    {
        JSONReader reader = new JSONValidatingReader(new ExceptionErrorListener());
        Object rootObj = reader.read(body);
        Map<?, ?> rootJson = (Map<?, ?>) rootObj;
        return (String) rootJson.get(BitmsConstants.SIGN);
    }
    
    /**
     * @see Converter#encryptSourceData(BasicRequest, String, String, String, String, String)
     */
    public String encryptSourceData(ApiBasicRequest<?> request, String body, String format, String encryptType, String encryptKey, String charset) throws ApiException
    {
        ResponseParseItem respSignSourceData = getJSONSignSourceData(request, body);
        String bodyIndexContent = body.substring(0, respSignSourceData.getStartIndex());
        String bodyEndContent = body.substring(respSignSourceData.getEndIndex());
        return bodyIndexContent + Encrypt.decryptContent(respSignSourceData.getEncryptContent(), encryptType, encryptKey) + bodyEndContent;
    }
    
    /**
     *  Get the JSON response tag content string
     *
     * @param request
     * @param body
     * @return
     */
    private ResponseParseItem getJSONSignSourceData(ApiBasicRequest<?> request, String body)
    {
        String rootNode = BitmsConstants.RESPONSE_KEY;
        int indexOfRootNode = body.indexOf(rootNode);
        if (indexOfRootNode > 0)
        {
            return parseJSONSignSourceData(body, rootNode, indexOfRootNode);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * 
     * Intercept the return content
     * @param body
     * @param rootNode
     * @param indexOfRootNode
     * @return
     */
    private ResponseParseItem parseJSONSignSourceData(String body, String rootNode, int indexOfRootNode)
    {
        int signDataStartIndex = indexOfRootNode + rootNode.length() + 2;
        int indexOfSign = body.indexOf("\"message\"");
        if (indexOfSign < 0)
        {
            indexOfSign = body.length();
        }
        int signDataEndIndex = indexOfSign - 1;
        String encryptContent = body.substring(signDataStartIndex + 1, signDataEndIndex - 1);
        return new ResponseParseItem(signDataStartIndex, signDataEndIndex, encryptContent);
    }
}
