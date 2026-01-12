package fr.asdepack.types;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@DatabaseTable(tableName = "kits")
@NoArgsConstructor
@AllArgsConstructor
public class Kit {
    @DatabaseField(id = true)
    @Getter
    @Setter
    private int id;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private String name;
    @DatabaseField(canBeNull = false, persisterClass =  ItemStackPersister.class)
    @Getter
    @Setter
    private ItemStack icon;
    @DatabaseField(canBeNull = false, persisterClass =  ItemStackListPersister.class)
    @Getter
    @Setter
    private List<ItemStack> items;
}
