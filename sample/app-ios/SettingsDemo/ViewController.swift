//
//  ViewController.swift
//  SettingsDemo
//
//  Created by Russell Wolf on 5/5/18.
//  Copyright © 2018 Russell Wolf. All rights reserved.
//

import UIKit
import Shared

class ViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate, UITextFieldDelegate {

    @IBOutlet var typePicker: UIPickerView?
    @IBOutlet var valueInput: UITextField?
    @IBOutlet var outputText: UILabel?

    lazy var settingsRepository = SharedSettingsRepository(settingsFactory: SharedSettingsFactory())

    override func viewDidLoad() {
        super.viewDidLoad()

        typePicker?.delegate = self
        typePicker?.dataSource = self
        valueInput?.delegate = self
    }

    func pickerView(_ pickerView: UIPickerView,
                    titleForRow row: Int,
                    forComponent component: Int) -> String? {
        return settingsRepository.mySettings[row].key
    }

    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }

    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return settingsRepository.mySettings.count
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
        let settingConfig = settingsRepository.mySettings[row]
        if (settingConfig.set(value: value)) {
            outputText?.text = ""
        } else {
            outputText?.text = "INVALID VALUE!"
        }
    }

    @IBAction func onGetButtonPress() {
        guard let row = typePicker?.selectedRow(inComponent: 0) else {
            return
        }
        let settingConfig = settingsRepository.mySettings[row]
        outputText?.text = settingConfig.get()
    }
    
    @IBAction func onRemoveButtonPress() {
        guard let row = typePicker?.selectedRow(inComponent: 0) else {
            return
        }
        let settingConfig = settingsRepository.mySettings[row]
        settingConfig.remove()
        outputText?.text = "Setting Removed!"
    }
    
    @IBAction func onClearButtonPress() {
        settingsRepository.clear()
        outputText?.text = "Settings Cleared!"
    }
}
