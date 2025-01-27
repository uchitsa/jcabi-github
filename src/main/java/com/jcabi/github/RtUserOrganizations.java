/**
 * Copyright (c) 2013-2025, jcabi.com
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
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;

/**
 * Github user organizations.
 * @see <a href="https://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.24
 * @checkstyle MultipleStringLiterals (500 lines)
 */
@Immutable
@Loggable(Loggable.DEBUG)
@EqualsAndHashCode(of = { "entry", "ghub", "owner" })
final class RtUserOrganizations implements UserOrganizations {

    /**
     * API entry point.
     */
    private final transient Request entry;

    /**
     * Github.
     */
    private final transient Github ghub;

    /**
     * User we're in.
     */
    private final transient User owner;

    /**
     * Public ctor.
     * @param github Github
     * @param req Request
     * @param user User
     */
    RtUserOrganizations(
        final Github github,
        final Request req,
        final User user
    ) {
        this.entry = req;
        this.ghub = github;
        this.owner = user;
    }

    @Override
    public Github github() {
        return this.ghub;
    }

    @Override
    public User user() {
        return this.owner;
    }

    @Override
    public Iterable<Organization> iterate() throws IOException {
        final String login = this.owner.login();
        return new RtPagination<>(
            this.entry.uri().path("/users").path(login).path("/orgs").back(),
            new OrganizationMapping(this.ghub.organizations())
        );
    }

    /**
     * Maps organization JSON objects to Organization instances.
     */
    private static final class OrganizationMapping
        implements RtValuePagination.Mapping<Organization, JsonObject> {
        /**
         * Organizations.
         */
        private final transient Organizations organizations;

        /**
         * Ctor.
         * @param orgs Organizations
         */
        OrganizationMapping(final Organizations orgs) {
            this.organizations = orgs;
        }

        @Override
        public Organization map(final JsonObject object) {
            return this.organizations.get(object.getString("login"));
        }
    }
}
