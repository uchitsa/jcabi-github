/**
 * Copyright (c) 2013-2024, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import lombok.EqualsAndHashCode;

/**
 * Github comment.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "request", "owner", "num" })
final class RtComment implements Comment {

    /**
     * Content field name.
     */
    private static final String CONTENT = "content";

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Issue we're in.
     */
    private final transient Issue owner;

    /**
     * Comment number.
     */
    private final transient long num;

    /**
     * Public ctor.
     * @param req RESTful request
     * @param issue Owner of this comment
     * @param number Number of the get
     */
    RtComment(final Request req, final Issue issue, final long number) {
        final Coordinates coords = issue.repo().coordinates();
        this.request = req.uri()
            .path("/repos")
            .path(coords.user())
            .path(coords.repo())
            .path("/issues")
            .path("/comments")
            .path(Long.toString(number))
            .back();
        this.owner = issue;
        this.num = number;
    }

    @Override
    public String toString() {
        return this.request.uri().get().toString();
    }

    @Override
    public Issue issue() {
        return this.owner;
    }

    @Override
    public long number() {
        return this.num;
    }

    @Override
    public void remove() throws IOException {
        this.request.method(Request.DELETE).fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.request).fetch();
    }

    @Override
    public void patch(final JsonObject json) throws IOException {
        new RtJson(this.request).patch(json);
    }

    @Override
    public int compareTo(
        final Comment comment
    ) {
        return Long.compare(this.number(), comment.number());
    }

    @Override
    public void react(final Reaction reaction) throws IOException {
        final JsonStructure json = Json.createObjectBuilder()
            .add(RtComment.CONTENT, reaction.type())
            .build();
        this.request.method(Request.POST)
            .body().set(json).back()
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
    }

    @Override
    public Iterable<Reaction> reactions() {
        return new RtPagination<>(
            this.request.uri().path("/reactions").back(),
            object -> new Reaction.Simple(object.getString(RtComment.CONTENT))
        );
    }
}
