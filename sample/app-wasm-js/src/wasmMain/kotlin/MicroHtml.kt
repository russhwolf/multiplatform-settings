/*
 * Copyright 2023 Russell Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.russhwolf.settings.example.wasmjs

import kotlinx.dom.appendElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLOutputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.Event

// DSL functions to mimic kotlinx.html until it gets wasm support

@DslMarker
annotation class MicroHtml

@MicroHtml
fun HTMLElement.div(body: HTMLDivElement.() -> Unit = {}) =
    appendElement("div") { (this as HTMLDivElement).body() } as HTMLDivElement

@MicroHtml
fun HTMLElement.select(body: HTMLSelectElement.() -> Unit = {}) =
    appendElement("select") { (this as HTMLSelectElement).body() } as HTMLSelectElement

@MicroHtml
fun HTMLElement.option(body: HTMLOptionElement.() -> Unit = {}) =
    appendElement("option") { (this as HTMLOptionElement).body() } as HTMLOptionElement

@MicroHtml
fun HTMLElement.input(body: HTMLInputElement.() -> Unit = {}) =
    appendElement("input") { (this as HTMLInputElement).body() } as HTMLInputElement

@MicroHtml
fun HTMLElement.button(body: HTMLButtonElement.() -> Unit = {}) =
    appendElement("button") { (this as HTMLButtonElement).body() } as HTMLButtonElement

@MicroHtml
fun HTMLElement.output(body: HTMLOutputElement.() -> Unit = {}) =
    appendElement("output") { (this as HTMLOutputElement).body() } as HTMLOutputElement

fun HTMLElement.onClick(block: (Event) -> Unit) = addEventListener("click", block)
