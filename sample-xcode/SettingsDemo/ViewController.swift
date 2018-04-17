//
//  ViewController.swift
//  SettingsDemo
//
//  Created by Russell Wolf on 5/5/18.
//  Copyright © 2018 Russell Wolf. All rights reserved.
//

import UIKit
import Kotlin

class ViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate, UITextFieldDelegate {

    @IBOutlet var typePicker: UIPickerView?
    @IBOutlet var valueInput: UITextField?
    @IBOutlet var outputText: UILabel?

    lazy var settingsRepository = KotlinSettingsRepository(settings: KotlinLibrarySettings())

    override func viewDidLoad() {
        super.viewDidLoad()

        typePicker?.delegate = self
        typePicker?.dataSource = self
        valueInput?.delegate = self
    }

    func pickerView(_ pickerView: UIPickerView,
                    titleForRow row: Int,
                    forComponent component: Int) -> String? {
        return SETTING_CONFIGS[row].label
    }

    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }

    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return SETTING_CONFIGS.count
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    @IBAction func onSetButtonPress() {
        guard let row = typePicker?.selectedRow(inComponent: 0) else {
            return
        }
        guard let value = valueInput?.text else {
            return
        }
        let settingConfig = SETTING_CONFIGS[row]
        if (settingConfig.set(settingsRepository, value)) {
            outputText?.text = ""
        } else {
            outputText?.text = "Invalid value!"
        }
    }

    @IBAction func onGetButtonPress() {
        guard let row = typePicker?.selectedRow(inComponent: 0) else {
            return
        }
        let settingConfig = SETTING_CONFIGS[row]
        outputText?.text = settingConfig.get(settingsRepository)
    }

    @IBAction func onClearButtonPress() {
        settingsRepository.clear()
        outputText?.text = "Settings Cleared!"
    }
}

private let SETTING_CONFIGS = [
    SettingConfig(
            label: "String",
            set: { (settingsRepository, value) -> Bool in
                settingsRepository.myStringSetting = value
                return true
            },
            get: { (settingsRepository) -> String in return settingsRepository.myStringSetting }
    ),
    SettingConfig(
            label: "Int",
            set: { (settingsRepository, value) -> Bool in
                guard let parsedValue = Int32(value) else {
                    return false
                }
                settingsRepository.myIntSetting = parsedValue
                return true
            },
            get: { (settingsRepository) -> String in return String(settingsRepository.myIntSetting) }
    ),
    SettingConfig(
            label: "Long",
            set: { (settingsRepository, value) -> Bool in
                guard let parsedValue = Int64(value) else {
                    return false
                }
                settingsRepository.myLongSetting = parsedValue
                return true
            },
            get: { (settingsRepository) -> String in return String(settingsRepository.myLongSetting) }
    ),
    SettingConfig(
            label: "Float",
            set: { (settingsRepository, value) -> Bool in
                guard let parsedValue = Float(value) else {
                    return false
                }
                settingsRepository.myFloatSetting = parsedValue
                return true
            },
            get: { (settingsRepository) -> String in return String(settingsRepository.myFloatSetting) }
    ),
    SettingConfig(
            label: "Double",
            set: { (settingsRepository, value) -> Bool in
                guard let parsedValue = Double(value) else {
                    return false
                }
                settingsRepository.myDoubleSetting = parsedValue
                return true
            },
            get: { (settingsRepository) -> String in return String(settingsRepository.myDoubleSetting) }
    ),
    SettingConfig(
            label: "Boolean",
            set: { (settingsRepository, value) -> Bool in
                guard let parsedValue = Bool(value) else {
                    return false
                }
                settingsRepository.myBooleanSetting = parsedValue
                return true
            },
            get: { (settingsRepository) -> String in return String(settingsRepository.myBooleanSetting) }
    )
]

private struct SettingConfig {
    let label: String
    let set: (_ settingsRepository: KotlinSettingsRepository, _ value: String) -> Bool
    let get: (_ settingsRepositoty: KotlinSettingsRepository) -> String
}
