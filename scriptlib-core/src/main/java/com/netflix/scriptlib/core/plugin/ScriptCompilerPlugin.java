/*
 *
 *  Copyright 2013 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.scriptlib.core.plugin;

import java.util.Set;

import com.netflix.scriptlib.core.compile.ScriptArchiveCompiler;

/**
 * Language plugin bootstrapper. Factory/provider interfaces for exporting classes needed for
 * loading a language plugin
 *
 * @author James Kojo
 */
public interface ScriptCompilerPlugin {

    public Set<? extends ScriptArchiveCompiler> getCompilers();
}
