/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mrql.gen;

import java_cup.runtime.*;
import java.io.*;
import java.util.ArrayList;

public class Main extends GenParser {
    public static void main ( String args[] ) throws Exception {
        ArrayList<String> files = new ArrayList<String>();
        boolean is_directory = false;
        String output = null;
        for ( int i = 0; i < args.length; i++ )
            if (args[i].equals("-encrypt"))
                Crypt.encryptp = true;
            else if (args[i].equals("-o"))
                output = args[++i];
            else files.add(args[i]);
        if (output != null && new File(output).isDirectory())
            is_directory = true;
        if (!is_directory && files.size() > 1)
            throw new Error("Expected an output directory: "+output);
        for ( String file: files )
            try {
                Meta.clear();
                scanner = new GenLex(new FileInputStream(file));
                String outfile = file.replace(".gen",".java");
                if (is_directory)
                    outfile = new File(output,new File(outfile).getName()).getPath();
                if (outfile.equals(file))
                    if (is_directory) {
                        System.err.println("Cannot compile: "+file);
                        continue;
                    } else outfile = output;
                out = new PrintStream(new FileOutputStream(outfile));
                out.print("/* DO NOT EDIT THIS FILE. THIS FILE WAS GENERATED FROM "+file+" BY GEN */");
                new GenParser(scanner).parse();
                Meta.dump_names(out);
                out.close();
            } catch (Error ex) {
                System.err.println(ex.getMessage()+" while parsing the GEN file: "+file);
                System.exit(-1);
            }
    }
}
