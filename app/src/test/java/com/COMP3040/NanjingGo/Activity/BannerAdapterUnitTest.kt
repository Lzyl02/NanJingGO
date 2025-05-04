import android.view.View
import android.widget.ImageView
import com.COMP3040.NanjingGo.Adapter.BannerAdapter
import com.COMP3040.NanjingGo.R
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the BannerAdapter.
 */
class BannerAdapterUnitTest {

    @Mock
    private lateinit var imageView: ImageView

    @Mock
    private lateinit var view: View

    private lateinit var adapter: BannerAdapter

    @Before
    fun setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)

        // Mock findViewById to return the mocked ImageView
        `when`(view.findViewById<ImageView>(R.id.bannerImage)).thenReturn(imageView)

        // Initialize the adapter with dummy banner data
        val banners = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)
        adapter = BannerAdapter(banners)
    }

    /**
     * Test the item count in the adapter.
     * Verifies that the adapter returns the correct number of items.
     */
    @Test
    fun testItemCount_WithDummyData_ReturnsCorrectCount() {
        // Assert that the item count matches the size of the banner list
        assertEquals(3, adapter.itemCount)
    }

    /**
     * Test the onBindViewHolder method.
     * Ensures that the correct banner resource is set for the given position.
     */
    @Test
    fun testOnBindViewHolder_WithValidPosition_BindsCorrectImage() {
        // Create a mock ViewHolder
        val viewHolder = adapter.BannerViewHolder(view)

        // Bind the item at position 1
        adapter.onBindViewHolder(viewHolder, 1)

        // Verify that the correct resource is set on the ImageView
        verify(imageView).setImageResource(R.drawable.banner2)
    }
}
