package ca.csf.mobile1.tp2.activity;

import ca.csf.mobile1.tp2.activity.Crypt.EncryptTask;
import ca.csf.mobile1.tp2.modele.Encrypt;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EncryptTaskTest
{
    private EncryptTask encryptTask;

    private String inputCharacters;
    private String outputCharacters;

    private boolean exceptionWasThrown;

    @Before
    public void setUp()
    {
        inputCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ .";
        outputCharacters = "xTHIaqZOXVzvSMRLhmB ldesKNnQrpDoigWb.GUEwCfPkAJtFcyujY";

        exceptionWasThrown = false;
    }

    @Test
    public void validateTestCharactersOk()
    {
        try
        {
            Encrypt.INSTANCE.encrypt("Bonjour les pommes", inputCharacters, outputCharacters);
            Encrypt.INSTANCE.encrypt("a", inputCharacters, outputCharacters);
            Encrypt.INSTANCE.encrypt("A", inputCharacters, outputCharacters);
            Encrypt.INSTANCE.encrypt("  ", inputCharacters, outputCharacters);
            Encrypt.INSTANCE.encrypt("AWDAWD", inputCharacters, outputCharacters);
            Encrypt.INSTANCE.encrypt("awdawd", inputCharacters, outputCharacters);
            Encrypt.INSTANCE.encrypt("pasdespace", inputCharacters, outputCharacters);
            Encrypt.INSTANCE.encrypt("qe", inputCharacters, outputCharacters);
            Encrypt.INSTANCE.encrypt("..", inputCharacters, outputCharacters);
        }
        catch (Exception e)
        {
            exceptionWasThrown = true;
        }

        assertFalse(exceptionWasThrown);
    }

    @Test
    public void validateTestLotOfCharacters()
    {
        String userStringToTest = "Lorem ipsum dolor sit amet consectetur adipiscing elit. Sed sit amet leo eget urna bibendum fringilla id a lectus. Nulla facilisi. Cras neque metus lobortis in fringilla a eleifend eget est. In gravida sapien et elit malesuada vehicula. Duis vel luctus nisl vel rutrum tortor. Curabitur vel massa nec mi mollis vulputate sit amet vel velit. Phasellus at posuere ipsum a condimentum erat. Praesent vitae dignissim lorem et facilisis nisi. Mauris pellentesque suscipit ligula ac rutrum. Vivamus fringilla vehicula justo quis aliquet lacus dignissim sit amet. Nulla facilisi. Pellentesque pellentesque bibendum tortor vel tincidunt. Ut nec auctor quam. Nulla ante nunc faucibus quis nunc sed eleifend tincidunt elit. Praesent vitae posuere urna ac volutpat dolor. Nulla eu odio ac lectus rutrum blandit. Fusce ac lacus nisi. Vestibulum ut est purus. Sed ultricies sem id leo facilisis non feugiat ex placerat. Sed convallis mollis metus ornare suscipit diam fringilla id. Nulla commodo congue dolor eu pharetra. Vestibulum vel ipsum id massa pellentesque suscipit. Etiam vitae fringilla ligula. Proin id odio luctus porta metus ac auctor lacus. In a porta nibh.Nulla nec lorem eget ante aliquam ullamcorper. Proin sit amet elit eu diam faucibus lobortis id et risus. Phasellus nulla ante malesuada eget accumsan in ultrices at dolor. Morbi eu laoreet sapien. Phasellus id enim pellentesque congue augue at ullamcorper nibh. Nulla bibendum tincidunt dictum. Nam tincidunt molestie odio eu consectetur. Nunc ac consequat dui. Pellentesque a eros vitae elit imperdiet hendrerit eu et elit. Pellentesque enim augue vehicula a tempor non condimentum eu eros. Integer et mollis turpis. Phasellus sit amet ultrices felis eu suscipit nunc. Nulla nibh nulla dapibus sit amet urna vitae pellentesque efficitur turpis. Phasellus eleifend molestie magna ac commodo. Sed suscipit justo ac pretium porttitor. Maecenas malesuada sapien purus vel pulvinar eros tempus eu. Donec posuere aliquam tortor vitae luctus tortor luctus et. Curabitur vel ornare felis. Maecenas et tempor nisl. Curabitur eu est at ante sodales accumsan. Cras sed nulla lacus. Aliquam turpis risus luctus quis egestas at placerat non risus. In gravida lorem sodales mollis metus vel placerat dui. Vivamus tincidunt diam nec magna tincidunt ullamcorper. Phasellus vitae nulla ante. Suspendisse imperdiet pharetra nunc.Aliquam et consectetur odio. In vitae diam enim. Vestibulum mollis ac erat ac eleifend. Donec mattis quam ac purus malesuada non ultricies odio hendrerit. Aliquam maximus lorem massa eu feugiat velit viverra eget. Sed vestibulum velit ac velit vestibulum sollicitudin. Integer sollicitudin arcu purus ac laoreet ipsum vehicula gravida. Donec nec neque sem. Morbi rhoncus posuere risus ut bibendum. Duis sagittis dapibus dui at iaculis. Donec eget lacinia erat. Praesent enim elit vehicula a gravida quis efficitur quis ante. Sed et pulvinar mi. Nam elit justo cursus eu fermentum et bibendum sed metus. Nunc at lectus eget tellus vulputate egestas ut vitae felis. Vivamus eu leo vitae velit dictum tincidunt. Morbi fringilla tempus tortor eu auctor quam volutpat at.Quisque sed sollicitudin est at efficitur nisi. Nam vitae nibh sit amet enim imperdiet vehicula. Integer sit amet metus lobortis commodo odio in efficitur lorem. Donec et lacus quam. Aenean sollicitudin magna nec malesuada gravida. Aliquam non est et urna tincidunt suscipit. Nulla a maximus metus sit amet rutrum augue. Aliquam blandit maximus nunc sit amet dapibus. Proin ac turpis ullamcorper pharetra sem a sodales lectus. Aliquam sodales urna sed elit aliquet commodo. Etiam vestibulum congue accumsan. In ac augue purus. Proin quis enim nec mauris cursus euismod. In hac habitasse platea dictumst. Aenean ullamcorper nulla eu mauris consequat ullamcorper interdum erat cursus. Vestibulum odio sapien vulputate et metus eu interdum finibus nulla. Cras consequat tristique ex a congue nulla sollicitudin eu. Nam iaculis elit in magna egestas tempor. Nunc tortor nulla viverra in nisl a lacinia vestibulum eros. Vestibulum et sapien ac eros varius pulvinar eu ut lorem. Maecenas pellentesque lorem eget sapien efficitur auctor. Quisque posuere commodo lorem quis mattis lorem tempus vitae. Aenean sit amet ligula non elit porttitor malesuada. Aliquam quam purus tincidunt sed hendrerit vitae lacinia at lectus. Cras cursus auctor nisl vitae placerat magna tristique quis. Duis aliquet lorem justo eget viverra tortor blandit et. Cras maximus interdum tortor vitae vestibulum risus facilisis vel. Etiam suscipit mollis enim sit amet porta. Vivamus suscipit libero at nibh rhoncus vel scelerisque augue elementum. In erat odio efficitur accumsan pretium sed dignissim aliquam ipsum. Sed a congue tortor non bibendum sapien. Nullam sed nisl nunc. Class aptent taciti sociosqu ad litora torquent per conubia nostra per inceptos himenaeos. Mauris ornare justo vitae lacus rhoncus laoreet. In cursus purus nec felis mollis commodo. Fusce tincidunt dolor et ipsum dapibus tristique. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae Quisque scelerisque lorem nibh sed egestas ligula iaculis ac. Aenean bibendum mi vehicula congue consectetur erat elit elementum diam nec fermentum ante mauris sed justo. Donec diam sem vehicula et tincidunt a hendrerit hendrerit arcu. Fusce quis tincidunt ligula. Sed nec pellentesque velit laoreet convallis augue. Fusce efficitur dui non nibh mattis ut consectetur felis vehicula. Maecenas at dictum diam. Etiam egestas venenatis erat at facilisis. Praesent dapibus tortor quis felis pharetra dapibus in et ante.Sed nec tortor venenatis fringilla dolor ac condimentum augue. Pellentesque ac elit eu leo auctor varius. Nullam ut fringilla ante. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam quis convallis lorem quis elementum nisl. Donec nisi urna congue ut lacus vel sollicitudin posuere ligula. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae Nam pulvinar eros a tempus laoreet. Quisque et turpis in ipsum lobortis lobortis. Ut congue justo libero vitae mollis leo hendrerit eu. Morbi nunc risus accumsan vel mauris quis dapibus dapibus ipsum. Etiam suscipit euismod commodo. Donec vitae egestas urna. Curabitur maximus quis magna ut hendrerit. Etiam non ante imperdiet facilisis nibh eget sollicitudin magna. Pellentesque viverra imperdiet molestie. Mauris vitae vehicula nibh a consectetur eros. Interdum et malesuada fames ac ante ipsum primis in faucibus. In hac habitasse platea dictumst. Suspendisse potenti. Phasellus rutrum dui eu dui dictum nec bibendum sapien fermentum. Nunc a pulvinar odio.Nam hendrerit dolor id est consequat semper. Mauris nec lacinia lectus. Donec nec urna ac orci vulputate vulputate ac eget odio. Aenean eget elementum orci at molestie leo. Ut interdum est in pellentesque consectetur. Aliquam erat volutpat. Quisque ac libero accumsan urna rhoncus blandit et eget magna. Nulla facilisi. Donec porttitor orci mattis venenatis rutrum. Nullam placerat porta leo finibus hendrerit magna finibus quis. Donec quis sagittis augue. Maecenas sit amet suscipit ante. Donec interdum congue erat eget accumsan. Maecenas a rhoncus metus. Nunc in gravida turpis a volutpat sem. Cras facilisis erat et suscipit sollicitudin. Praesent tristique neque et sollicitudin interdum nibh nunc interdum sem viverra malesuada risus felis quis nisl. Praesent porta nisl eros quis lacinia sapien porta vitae. Suspendisse potenti. Vestibulum nec convallis quam. Donec nec est vitae orci commodo ultricies. Nulla gravida augue ac dui aliquet ac pharetra urna varius. Suspendisse quis mauris luctus scelerisque elit eget auctor odio. Pellentesque a iaculis leo et vestibulum mi. Maecenas scelerisque scelerisque diam et viverra. Nunc sagittis lorem eu dapibus ultricies. Cras euismod massa at interdum facilisis eros libero tristique urna sed pellentesque metus elit non elit. Vestibulum aliquet dapibus gravida. Pellentesque ac dignissim tortor vitae vestibulum mi. Nam sodales lorem sed mattis tristique est tellus euismod quam in interdum lectus nulla vitae enim. Duis posuere mauris in convallis interdum. Donec suscipit volutpat sapien. Nunc nec lectus augue. Sed dapibus dui vel cursus tempor. Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque sodales porttitor turpis a convallis libero varius at. Quisque luctus nisi non dui congue vestibulum.";

        try
        {
            Encrypt.INSTANCE.encrypt(userStringToTest, inputCharacters, outputCharacters);
        }
        catch (Exception e)
        {
            exceptionWasThrown = true;
        }

        assertFalse(exceptionWasThrown);
    }

    @Test
    public void validateTestCharactersBad01()
    {
        try
        {
            Encrypt.INSTANCE.encrypt("Bonjour lé pommes", inputCharacters, outputCharacters);
        }
        catch (Exception e)
        {
            exceptionWasThrown = true;
        }

        assertTrue(exceptionWasThrown);
    }

    @Test
    public void validateTestCharactersBad02()
    {
        try
        {
            Encrypt.INSTANCE.encrypt("Bonjour_le pommes", inputCharacters, outputCharacters);
        }
        catch (Exception e)
        {
            exceptionWasThrown = true;
        }

        assertTrue(exceptionWasThrown);
    }

    @Test
    public void validateTestCharactersBad03()
    {
        try
        {
            Encrypt.INSTANCE.encrypt("Bonjour , pommes", inputCharacters, outputCharacters);
        }
        catch (Exception e)
        {
            exceptionWasThrown = true;
        }

        assertTrue(exceptionWasThrown);
    }

    @Test
    public void validateTestCharactersBad04()
    {
        try
        {
            Encrypt.INSTANCE.encrypt("Bonjour le 1ommes", inputCharacters, outputCharacters);
        }
        catch (Exception e)
        {
            exceptionWasThrown = true;
        }

        assertTrue(exceptionWasThrown);
    }

    @Test
    public void validateTestEmptyString()
    {
        try
        {
            Encrypt.INSTANCE.encrypt("", inputCharacters, outputCharacters);
        }
        catch (Exception e)
        {
            exceptionWasThrown = true;
        }

        assertTrue(exceptionWasThrown);
    }

    @Test
    public void validateTestNullString()
    {
        try
        {
            Encrypt.INSTANCE.encrypt(null, inputCharacters, outputCharacters);
        }
        catch (Exception e)
        {
            exceptionWasThrown = true;
        }

        assertTrue(exceptionWasThrown);
    }
}