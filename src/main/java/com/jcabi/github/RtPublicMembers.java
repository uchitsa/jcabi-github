/**
 * Copyright (c) 2013-2025 Yegor Bugayenko
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
import lombok.EqualsAndHashCode;
import org.hamcrest.Matchers;

/**
 * Public members of a GitHub organization.
 *
 * @see <a href="https://developer.github.com/v3/orgs/members/">Organization Members API</a>
 * @since 0.24
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "organization" })
public final class RtPublicMembers implements PublicMembers {
    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * RESTful request.
     */
    private final transient Request request;

    /**
     * Organization.
     */
    private final transient Organization organization;

    /**
     * Public ctor.
     * @param req Request
     * @param organ Organization
     */
    public RtPublicMembers(
        final Request req,
        final Organization organ
    ) {
        this.entry = req;
        this.request = req.uri()
            .path("/orgs")
            .path(organ.login())
            .path("public_members")
            .back();
        this.organization = organ;
    }

    @Override
    public Organization org() {
        return this.organization;
    }

    @Override
    public void conceal(
        final User user
    ) throws IOException {
        this.request
            .uri().path(user.login()).back()
            .method(Request.DELETE)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public void publicize(
        final User user
    ) throws IOException {
        this.request
            .uri().path(user.login()).back()
            .method(Request.PUT)
            .fetch().as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<User> iterate() {
        return new RtPagination<>(
            this.request,
            object -> new RtUser(
                this.organization.github(),
                this.entry,
                object.getString("login")
            )
        );
    }

    @Override
    public boolean contains(
        final User user
    ) throws IOException {
        return this.request
            .uri().path(user.login()).back()
            .method(Request.GET)
            .fetch().as(RestResponse.class)
            .assertStatus(
                Matchers.is(
                    Matchers.oneOf(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
            )
        )
            .status() == HttpURLConnection.HTTP_NO_CONTENT;
    }
}
