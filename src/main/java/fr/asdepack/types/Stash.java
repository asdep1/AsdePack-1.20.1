package fr.asdepack.types;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.asdepack.types.serializers.ItemStackListPersister;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.world.item.ItemStack;

import java.io.Serializable;
import java.util.List;

@DatabaseTable(tableName = "stash")
@NoArgsConstructor
@AllArgsConstructor
public class Stash implements Serializable {
    @DatabaseField(generatedId = true)
    @Getter
    @Setter
    private int id;
    @DatabaseField(canBeNull = false)
    @Getter
    @Setter
    private String playeruuid;
    @DatabaseField(canBeNull = false, persisterClass = ItemStackListPersister.class)
    @Getter
    @Setter
    private List<ItemStack> items;

}
