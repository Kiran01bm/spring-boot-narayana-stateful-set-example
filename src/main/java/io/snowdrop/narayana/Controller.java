/*
 * Copyright 2016-2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.snowdrop.narayana;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.List;

/**
 * @author <a href="mailto:gytis@redhat.com">Gytis Trikleris</a>
 */
@RestController
public class Controller {

    private final EntriesService entriesService;

    @Autowired
    public Controller(EntriesService entriesService) {
        this.entriesService = entriesService;
    }

    @GetMapping
    @ResponseBody
    public List<Entry> getEntries() throws Exception {
        List<Entry> entries = entriesService.getAll();
        System.out.printf("Returning entries [%s] from host '%s'\n", entries, InetAddress.getLocalHost().getHostName());
        return entriesService.getAll();
    }

    @PostMapping
    public void createEntry(@RequestParam("entry") String entry) throws Exception {
        System.out.printf("Creating entry '%s' on host '%s'\n", entry, InetAddress.getLocalHost().getHostName());
        entriesService.create(entry);
    }

}