/*
 * SonarQube
 * Copyright (C) 2009-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.db.migration.version.v84.users.fk.properties;

import java.sql.SQLException;
import java.sql.Types;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.core.util.Uuids;
import org.sonar.db.CoreDbTester;
import org.sonar.server.platform.db.migration.step.DdlChange;

import static org.assertj.core.api.Assertions.assertThat;

public class AddUserUuidColumnToPropertiesUsersTest {

  @Rule
  public CoreDbTester db = CoreDbTester.createForSchema(AddUserUuidColumnToPropertiesUsersTest.class, "schema.sql");

  private DdlChange underTest = new AddUserUuidColumnToPropertiesUsers(db.database());

  @Before
  public void setup() {
    insertProperty(Uuids.createFast());
    insertProperty(Uuids.createFast());
    insertProperty(Uuids.createFast());
  }

  @Test
  public void add_uuid_column() throws SQLException {
    underTest.execute();

    db.assertColumnDefinition("properties", "user_uuid", Types.VARCHAR, 255, true);

    assertThat(db.countSql("select count(*) from properties"))
      .isEqualTo(3);
  }

  private void insertProperty(String uuid) {
    db.executeInsert("properties",
      "uuid", uuid,
      "prop_key", "kee-" + uuid,
      "is_empty", false,
      "created_at", System.currentTimeMillis());
  }

}
