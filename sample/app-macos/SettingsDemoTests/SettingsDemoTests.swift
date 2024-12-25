import XCTest
@testable import SettingsDemo
import Shared

final class SettingsDemoTests: XCTestCase {

    func testMigration() throws {
        let legacySettings = KeychainSettings(service: "Legacy Settings Test")
        legacySettings.clear()
        
        legacySettings.putString(key: "Foo", value: "Bar")
        
        let migratedSettings = KeychainSettings(service: "Legacy Settings Test", accessibility: KeychainSettings.Accessibility.afterfirstunlock)
        do {
            try migratedSettings.migrateLegacyKeys(keys: KotlinArray(size: 0, init: { _ in "" }))
        } catch {
            XCTFail("Migration failed: " + error.localizedDescription)
        }

        XCTAssertEqual("Bar", migratedSettings.getStringOrNull(key: "Foo"))
    }
}
