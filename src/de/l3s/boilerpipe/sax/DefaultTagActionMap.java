/**
 * boilerpipe
 *
 * Copyright (c) 2009, 2010 Christian Kohlschütter
 *
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.l3s.boilerpipe.sax;


/**
 * Default {@link TagAction}s. Seem to work well.
 * 
 * @see TagActionMap
 */
public class DefaultTagActionMap extends TagActionMap {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final TagActionMap INSTANCE = new DefaultTagActionMap();

    protected DefaultTagActionMap() {
        setTagAction("STYLE", CommonTagActions.TA_IGNORABLE_ELEMENT);
        setTagAction("SCRIPT", CommonTagActions.TA_IGNORABLE_ELEMENT);
        setTagAction("OPTION", CommonTagActions.TA_IGNORABLE_ELEMENT);
        setTagAction("OBJECT", CommonTagActions.TA_IGNORABLE_ELEMENT);
        setTagAction("EMBED", CommonTagActions.TA_IGNORABLE_ELEMENT);
        setTagAction("APPLET", CommonTagActions.TA_IGNORABLE_ELEMENT);
        setTagAction("LINK", CommonTagActions.TA_IGNORABLE_ELEMENT);

        setTagAction("A", CommonTagActions.TA_ANCHOR_TEXT);
        setTagAction("BODY", CommonTagActions.TA_BODY);

        setTagAction("STRIKE", CommonTagActions.TA_INLINE_NO_WHITESPACE);
        setTagAction("U", CommonTagActions.TA_INLINE_NO_WHITESPACE);
        setTagAction("B", CommonTagActions.TA_INLINE_NO_WHITESPACE);
        setTagAction("I", CommonTagActions.TA_INLINE_NO_WHITESPACE);
        setTagAction("EM", CommonTagActions.TA_INLINE_NO_WHITESPACE);
        setTagAction("STRONG", CommonTagActions.TA_INLINE_NO_WHITESPACE);
        setTagAction("SPAN", CommonTagActions.TA_INLINE_NO_WHITESPACE);
        
        // New in 1.1 (especially to improve extraction quality from Wikipedia etc.)
        setTagAction("SUP", CommonTagActions.TA_INLINE_NO_WHITESPACE);

        setTagAction("ABBR", CommonTagActions.TA_INLINE_WHITESPACE);
        setTagAction("ACRONYM", CommonTagActions.TA_INLINE_WHITESPACE);

        setTagAction("FONT", CommonTagActions.TA_INLINE_NO_WHITESPACE); // could also use TA_FONT 
    }
}
