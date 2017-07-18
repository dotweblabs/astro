package com.dotweblabs.astro.resource;

import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * @author <a href="mailto:kerby@dotweblabs.com">Kerby Martino</a>
 * @version 0-SNAPSHOT
 * @since 0-SNAPSHOT
 */
public interface KeyValueResource extends BaseResource {
    @Get
    Representation get(Representation representation);
    @Delete
    Representation delete(Representation representation);
    @Put
    Representation put(Representation representation);
}
