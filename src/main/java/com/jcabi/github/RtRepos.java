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
import com.jcabi.http.response.JsonResponse;
import com.jcabi.http.response.RestResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import lombok.EqualsAndHashCode;

/**
 * Github repositories.
 *
 * @since 0.8
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "ghub", "entry" })
final class RtRepos implements Repos {

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * RESTful entry.
     */
    private final transient Request entry;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     */
    RtRepos(final Github github, final Request req) {
        this.ghub = github;
        this.entry = req;
    }

    @Override
    public String toString() {
        return this.entry.uri().get().toString();
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public Repo create(final RepoCreate settings) throws IOException {
        String uriPath = "user/repos";
        final String org = settings.organization();
        if (org != null && !org.isEmpty()) {
            uriPath = "/orgs/".concat(org).concat("/repos");
        }
        return this.get(
            new Coordinates.Simple(
                this.entry.uri().path(uriPath)
                    .back().method(Request.POST)
                    .body().set(settings.json()).back()
                    .fetch().as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_CREATED)
                    .as(JsonResponse.class)
                    // @checkstyle MultipleStringLiterals (1 line)
                    .json().readObject().getString("full_name")
            )
        );
    }

    @Override
    public Repo get(final Coordinates name) {
        return new RtRepo(this.ghub, this.entry, name);
    }

    @Override
    public void remove(
        final Coordinates coords) throws IOException {
        this.entry.uri().path("/repos")
            .back().method(Request.DELETE)
            .uri().path(coords.toString()).back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_NO_CONTENT);
    }

    @Override
    public Iterable<Repo> iterate(
        final String identifier) {
        return new RtPagination<>(
            this.entry.uri().queryParam("since", identifier).back(),
            object -> this.get(
                new Coordinates.Simple(object.getString("full_name"))
            )
        );
    }

    @Override
    public boolean exists(final Coordinates coords) throws IOException {
        final String repo = coords.user().concat("/").concat(coords.repo());
        final RestResponse response = this.entry.uri()
            .path("/repos/".concat(repo)).back()
            .method(Request.GET).fetch().as(RestResponse.class);
        return response.status() == HttpURLConnection.HTTP_OK;
    }
}
