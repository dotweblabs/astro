package com.dotweblabs.astro.resource.jee;

import com.google.common.io.ByteStreams;
import com.dotweblabs.astro.resource.KeyValueResource;
import com.dotweblabs.astro.AstroClient;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public class KeyValueServerResource extends BaseServerResource
        implements KeyValueResource {

    private static final String[] address = { "127.0.0.1:5000" };
    private AstroClient client;

    @Override
    protected void doInit() {
        super.doInit();
        client = new AstroClient(address);
    }

    @Override
    public Representation post(Representation entity) {
        try {
            if(entity == null || entity.isEmpty()){
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return null;
            }
            if(key == null || key.isEmpty()){
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return null;
            }
            byte[] value = ByteStreams.toByteArray(entity.getStream());
            client.put(key, value);
            setStatus(Status.SUCCESS_OK);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return null;
    }

    @Override
    public Representation get(Representation representation) {
        Representation response = null;
        try {
            if(key == null || key.isEmpty()){
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return null;
            }
            Object value = client.get(key);
            if(value != null) {
                byte[] valueByteArray = toByteArray(value);
                response = new ByteArrayRepresentation(valueByteArray);
            }
            setStatus(Status.SUCCESS_OK);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return response;
    }

    @Override
    public Representation delete(Representation representation) {
        try {
            if(key == null || key.isEmpty()){
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                return null;
            }
            client.delete(key);
            setStatus(Status.SUCCESS_OK);
        } catch (Exception e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return null;
    }

    @Override
    public Representation put(Representation representation) {
        setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
        return null;
    }


}
