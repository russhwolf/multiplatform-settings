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

import SwiftUI
import Shared

struct ContentView: View {
    
    private let userDefaultsSettingsRepository: SettingsRepository = SettingsRepository(settings: NSUserDefaultsSettings(delegate: UserDefaults.standard))
    
    private let keychainSettingsRepository: SettingsRepository = SettingsRepository(settings: KeychainSettings(service: "Settings Demo"))

    @State
    var settingsRepository: SettingsRepository
    
    @State
    var selectedIndex: Int = 0
    
    @State
    var value: String = ""

    @State
    var enableLogging = false

    @State
    var outputText: String = ""
    
    init() {
        settingsRepository = userDefaultsSettingsRepository
    }
    
    func selectedItem() -> SettingConfig<AnyObject> {
        settingsRepository.mySettings[selectedIndex]
    }
        
    var body: some View {
        NavigationView {
            VStack(alignment: .leading) {
                Picker("Setting", selection: $selectedIndex) {
                    ForEach(settingsRepository.mySettings, id: \.key) { settingConfig in
                        Text(settingConfig.key).tag(settingsRepository.mySettings.firstIndex(of: settingConfig) ?? 0)
                    }
                }
                .onChange(of: selectedIndex, perform: { newValue in
                    enableLogging = selectedItem().isLoggingEnabled
                })
                .padding(.vertical, 8)
                
                TextField("Value", text: $value)
                    .padding(.vertical, 8)
                    .textFieldStyle(.roundedBorder)
                
                Button("Set Value") {
                    if (selectedItem().set(value: value)) {
                        outputText = ""
                    } else {
                        outputText = "INVALID VALUE!"
                    }
                }
                .padding(.vertical, 8)
                
                Button("Get Value") {
                    outputText = selectedItem().get()
                }
                .padding(.vertical, 8)
                
                Button("Remove Value") {
                    selectedItem().remove()
                    outputText = "Setting Removed!"
                }
                .padding(.vertical, 8)
                
                Button("Clear All Values") {
                    settingsRepository.clear()
                    outputText = "Settings Cleared!"
                }
                .padding(.vertical, 8)
                
                Toggle("Enable Logging", isOn: $enableLogging)
                    .padding(.vertical, 8)
                    .opacity(settingsRepository == userDefaultsSettingsRepository ? 1 : 0)
                    .onChange(of: enableLogging) { newValue in
                        selectedItem().isLoggingEnabled = newValue
                    }
                
                Text(outputText)
                    .padding(.vertical, 8)
                
                Picker("", selection: $settingsRepository) {
                    Text("User Defaults").tag(userDefaultsSettingsRepository)
                    Text("Keychain").tag(keychainSettingsRepository)
                }
                .pickerStyle(.segmented)
                .padding(.vertical, 8)
                .onChange(of: settingsRepository, perform: { newRepository in
                    enableLogging = selectedItem().isLoggingEnabled
                })
                
                Spacer()
            }
            .padding()
            .navigationTitle("Settings Demo")
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
