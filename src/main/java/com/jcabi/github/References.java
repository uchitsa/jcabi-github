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
import java.io.IOException;

/**
 * Github Git Data References.
 *
 * @since 0.8
 * @see <a href="https://developer.github.com/v3/git/references/">References API</a>
 */
@Immutable
public interface References {

    /**
     * Owner of them.
     * @return Repo
     */
    Repo repo();

    /**
     * Creates a reference.
     * @param ref The name of the fully qualified reference (ie: refs/heads/master).
     * @param sha The SHA1 value to set this reference to.
     * @return Reference - The newly created Reference
     * @throws IOException - If there are any errors.
     */
    Reference create(
        String ref,
        String sha
    ) throws IOException;

    /**
     * Get Reference by identifier.
     * @param identifier Reference's name.
     * @return Reference The reference with the given name
     */
    Reference get(
        String identifier
    );

    /**
     * Iterates all references.
     * @return Iterator of references.
     */
    Iterable<Reference> iterate();

    /**
     * Iterates references in sub-namespace.
     * @param subnamespace Sub-namespace
     * @return Iterator of references.
     */
    Iterable<Reference> iterate(
        String subnamespace
    );

    /**
     * Iterate references under "tags" sub-namespace.
     * @return Iterator of references.
     */
    Iterable<Reference> tags();

    /**
     * Iterate references under "heads" sub-namespace.
     * @return Iterator of references.
     */
    Iterable<Reference> heads();

    /**
     * Removes a reference by its identifier.
     * @param identifier Reference's identifier.
     * @throws IOException If there is any I/O problem.
     */
    void remove(
        String identifier
    ) throws IOException;
}
