/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.core.internal.scribe;

import com.google.gson.annotations.SerializedName;

public class EventNamespace {

    @SerializedName("client")
    public final String client;

    @SerializedName("page")
    public final String page;

    @SerializedName("section")
    public final String section;

    @SerializedName("component")
    public final String component;

    @SerializedName("element")
    public final String element;

    @SerializedName("action")
    public final String action;

    public EventNamespace(String client, String page, String section, String component,
            String element, String action) {
        this.client = client;
        this.page = page;
        this.section = section;
        this.component = component;
        this.element = element;
        this.action = action;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("client=").append(client)
                .append(", page=").append(page)
                .append(", section=").append(section)
                .append(", component=").append(component)
                .append(", element=").append(element)
                .append(", action=").append(action)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final EventNamespace that = (EventNamespace) o;

        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (component != null ? !component.equals(that.component) : that.component != null) {
            return false;
        }
        if (element != null ? !element.equals(that.element) : that.element != null) {
            return false;
        }
        if (page != null ? !page.equals(that.page) : that.page != null) return false;
        if (section != null ? !section.equals(that.section) : that.section != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = client != null ? client.hashCode() : 0;
        result = 31 * result + (page != null ? page.hashCode() : 0);
        result = 31 * result + (section != null ? section.hashCode() : 0);
        result = 31 * result + (component != null ? component.hashCode() : 0);
        result = 31 * result + (element != null ? element.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    public static class Builder {

        private String client;
        private String page;
        private String section;
        private String component;
        private String element;
        private String action;

        public Builder setClient(String client) {
            this.client = client;
            return this;
        }

        public Builder setPage(String page) {
            this.page = page;
            return this;
        }

        public Builder setSection(String section) {
            this.section = section;
            return this;
        }

        public Builder setComponent(String component) {
            this.component = component;
            return this;
        }

        public Builder setElement(String element) {
            this.element = element;
            return this;
        }

        public Builder setAction(String action) {
            this.action = action;
            return this;
        }

        public EventNamespace builder() {
            return new EventNamespace(client, page, section, component, element, action);
        }
    }
}
